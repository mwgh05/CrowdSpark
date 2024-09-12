import React, {useEffect, useState} from 'react';
import './styles/Foro.css'

import app from "../firebaseConfig.js";
import { getDatabase, ref, push, update, onValue, get } from "firebase/database"

import { useParams } from 'react-router-dom';

export default function Foro() {

    const [inputValue, setInputValue] = useState('');
    const [mensaje, setMensaje] = useState([]);
    const { usuarioID, proyectoID } = useParams();
  
    

    const publicarMensaje = async () => {
        if (!inputValue) return;

        const db = getDatabase(app);
        const dbRef = ref(db, `foros/${proyectoID}/mensajes`);  
        const newMessage = push(dbRef);

        await update(newMessage, {
            usuario: usuarioID,
            contenido: inputValue
        })

        setInputValue('');
    };
   
    useEffect(() => {
        const fetchData = async() => {
            const db = getDatabase(app);
            const dbRef = ref(db, `foros/${proyectoID}/mensajes`);  
    
            const usuariosRef = ref(db, `usuarios`);
            const userSnapshot = await get(usuariosRef);
            const userNames = userSnapshot.val() || {};
    
            const userIdToName = {};
            Object.keys(userNames).forEach(userId => {
            userIdToName[userId] = userNames[userId].nombre; // Assuming 'name' is the field for the user's name
            });
    
            const unsubscribe = onValue(dbRef, (snapshot) => {
                if (snapshot.exists()) {
                    const fetchMessages = snapshot.val() || [];
                    const messagesWithNames = Object.keys(fetchMessages).map(msgId => ({
                        ...fetchMessages[msgId],
                        usuario: userIdToName[fetchMessages[msgId].usuario] || fetchMessages[msgId].usuario // Fallback to ID if name not found
                    }));
                    setMensaje(messagesWithNames);
                } else {
                    console.error("No values.")
                }
            });
            return unsubscribe;
        };

        fetchData();
    }, [proyectoID]);

    return (
        <section className='App-content'>
            <div className='chatBox'>
                <div className='chatBoxField'>
                    {Object.keys(mensaje).map((msg) => (
                        <div>
                            <p>{mensaje[msg].usuario} : {mensaje[msg].contenido}</p>
                        </div>
                    ))}
                </div>
            </div>
            <div className='inputTextField'>
                <input type="text" 
                className='textbox' 
                name="message" 
                placeholder='Mensaje' 
                required={true}
                value={inputValue}
                onChange={(event) => setInputValue(event.target.value)}/>
                <button className='buttonn' type="submit" onClick={publicarMensaje}>✈</button>
            </div>
        </section>
    )

}