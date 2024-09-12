import React, { useState, useEffect } from 'react';
import './styles/CambiarContraseña.css';
import { FaEye, FaEyeSlash } from 'react-icons/fa'

//import axios from 'axios'; //Importamos el encargado de respuestas de api
//import { URL } from './data.js'

//Base de datos por firebase
import app from "../firebaseConfig.js";
import { getDatabase, ref, update, get } from "firebase/database"

import { useNavigate } from 'react-router-dom';

export default function CambiarContraseña() {
    const [usuarioCambio, setUsuarioCambio] = useState({
        correo: "",
        password: "",
        rechecarPassword: ""
    });

    const [usuarios, setUsuarios] = useState([]);

    //Hacemos que se pueda mostrar o no
    const [showPassword, setShowPassword] = useState(false);

    const navigate = useNavigate();

    useEffect(() => {
        const fetchAllUsers = async() => {
            //Tomamos la base de datos a la cual nos conectamos
            const db = getDatabase(app);

            //Referenciamos la base de datos, la ubicación donde guardamos los usuarios
            const dbRef = ref(db, "usuarios");

            //Buscamos información dentro de los casos, mediante un snapshot
            const snapshot = await get(dbRef);

            if (snapshot.exists()) {
                //Acá vamos a agarrar los datos junto al id como tal
                const myData = snapshot.val();
                /*Se observa de ésta manera:
                -Ntxj3bMCCnRtFWxzD12 (id a sacar)
                    correo
                    nombre
                    password
                */

                //Primero debemos de agarrar todas las llaves como tal
                const temporaryArray = Object.keys(myData).map( myId => {
                    return {
                        ...myData[myId],
                        usuarioID: myId
                    }
                });
                /*Tendríamos algo como ésto sin el map: 
                [Ntxj3bMCCnRtFWxzD12, ...]
                
                Con el mapeado tendremos ahora el dato de acuerdo al array, y la id como tal
                "mauHernandez@gmail.com", "Michael", "m4uH3rn4nd3z", Ntxj3bMCCnRtFWxzD12
                */

                setUsuarios(temporaryArray);
                console.log("Todos los datos de usuarios: ", temporaryArray);
                /*Inicialmente sin el snapshot.val, tenemos datos estilo json, luego con éste, tendríamos un 
                objeto de javascript, y finalmente con Object.values tendríamos un array de objetos JavaScript*/
            } else{
                alert("No hay valores dentro de nuestra tabla de base de datos");
            }
        };
        fetchAllUsers();
    }, []);

    //Función para establecer cambios
    const handleUserChange = (event) => {
        setUsuarioCambio(
          prev => ({...prev, [event.target.name]: event.target.value})
        );
      }

    const handleCambioContraseña = async(event) => {
        event.preventDefault(); //Evitamos reinicio de pantalla

        try {
            //Checar todos los cambios completos
            if (!usuarioCambio.correo || !usuarioCambio.password || !usuarioCambio.rechecarPassword){
                alert("Por favor, completar todos los campos");
                return;
            }

            //Checamos si las 2 contraseñas son iguales
            if (usuarioCambio.password !== usuarioCambio.rechecarPassword) {
                alert("La contraseña ingresada debe ser igual en ambos campos");
                return;
            }

            //Encontrar el usuario por correo electrónico
            const usuario = usuarios.find((user) => user.correo === usuarioCambio.correo );

            //Checar si el usuario existe
            if (!usuario) {
                alert("El correo electrónico ingresado no existe en la Base de Datos");
                return;
            }

            //Checamos si la contraseña usada nueva es la misma al anterior
            if (usuarioCambio.password === usuario.password){
                alert("La contraseña ingresada es igual a la anterior usada");
                return;
            }

            //Actualizar la contraseña usando la id de usuario
            const db = getDatabase(app);
            const updUsuarioRef = ref(db, "usuarios/"+usuario.usuarioID);
            await update(updUsuarioRef, {
                password: usuarioCambio.password
            });

            //Actualización se ha hecho de forma correcta
            alert("Se ha actualizado correctamente la contraseña");
            navigate("/iniciarSesion");
        }catch(error){
            console.error("Ha aparecido un error: ", error);
            alert("Error al actualizar la contraseña");
        }
    }

    const togglePasswordVissibility = () => {
        setShowPassword(!showPassword);
    }

    return (
        <section className="App-content">
            <form className='Form'>
                <div className='Elemento'>
                    <label className='Titulo'>Modificar contraseña</label>
                </div>

                <div className='Elemento'>
                    <label>Escriba el correo</label>
                    <input type={'email'} placeholder='mauHernandez@gmail.com' name='correo'
                    onChange={handleUserChange}/>
                </div>

                <div className='Elemento'>
                    <label>Escriba la contraseña</label>
                    <div className='password-input'>
                        <input 
                            type={showPassword ? 'text' : 'password'} 
                            placeholder='**********' 
                            name='password'
                            onChange={handleUserChange}
                        />
                        {/*Activa ícono de visualización*/}
                        {showPassword ? (
                            <FaEyeSlash className='eye-icon' onClick={togglePasswordVissibility}/>
                        ) : (
                            <FaEye className='eye-icon' onClick={togglePasswordVissibility}/>
                        )}
                    </div>
                </div>    

                <div className='Elemento'>
                    <label>Escriba de nuevo la contraseña</label>
                    <div className='password-input'>
                        <input 
                            type={showPassword ? 'text' : 'password'} 
                            placeholder='**********' 
                            name='rechecarPassword'
                            onChange={handleUserChange}
                        />
                        {/*Activa ícono de visualización*/}
                        {showPassword ? (
                            <FaEyeSlash className='eye-icon' onClick={togglePasswordVissibility}/>
                        ) : (
                            <FaEye className='eye-icon' onClick={togglePasswordVissibility}/>
                        )}
                    </div>
                </div>

                <div className='Elemento'>
                    <button onClick={handleCambioContraseña}>Aplicar cambios</button>
                </div>
            </form>
        </section>
    )
}