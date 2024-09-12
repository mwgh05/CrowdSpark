import React, { useState, useEffect } from 'react';

//Parte gráfica
import './styles/EnlistarTareas.css';
import logo from '../images/icon.png';

import {useNavigate} from "react-router-dom";

/*Como tal, la plataforma, sea desde registro o desde iniciar sesión, siempre recibe una id de usuario, ésta
id nos servirá para sellar el usuario del sistema a usar, será el parámetro id de usuario*/
import { useParams } from 'react-router-dom';

//Base de datos por firebase
import app from "../firebaseConfig.js";
import { getDatabase, get, ref} from 'firebase/database';

export default function EnlistarTareas() {
    const { usuarioID, proyectoID } = useParams();

    //Usamos el state para restaurar datos o información
    const [usuarioActual, setUsuarioActual] = useState(null);
    //const [proyectoActual, setProyectoActual] = useState(null);

    const [tareas, setTareas] = useState([]);

    const navigate = useNavigate();

    //Sacamos los datos de los usuarios
    useEffect(() => {
        const fetchUsuario = async() => {
            try {
                //Primero seteamos la base de datos como tal, checando directamente la id
                const db = getDatabase(app);
                const dbRef = ref(db, `usuarios/${usuarioID}`);

                //Tomamos la data directamente de dicho usuarioID
                const snapshot = await get(dbRef);

                //Checamos si existe un snapshot con dicha información específica
                if (snapshot.exists()) {
                    //Extraemos la información del usuario del snapshot
                    const userData = snapshot.val();

                    //Seteamos la data de usuario en nuestro estado global de usuario
                    setUsuarioActual(userData);
                    console.log("Dentro de crearTarea, si agarramos usuario");
                }else{
                    console.log("No se encontró un usuario con dicha información")
                }
            }catch(error){
                console.error("Error consiguiendo la información del usuario")
            }
        };

        fetchUsuario();
    }, [usuarioID]);

    //Array de todos los usuarios como tal dentro del proyecto
    useEffect(() => {
        const fetchUserData = async (userID) => {
            try {
                const db = getDatabase(app);
                const dbRef = ref(db, `usuarios/${userID}`);
                const snapshot = await get(dbRef);
    
                if (snapshot.exists()){
                    return snapshot.val()
                } else {
                    console.log(`No se encontró el usuario con la id ${userID}`);
                    return null;
                }
            }catch(error){
                console.error("Error consiguiendo la información del usuario")
            }
        }

        const fetchTareas = async() => {

            try{
                const db = getDatabase(app);
                const dbRef = ref(db, `tareas`);
                const snapshot = await get(dbRef);

                if (snapshot.exists()){
                    const tareasData = snapshot.val();
                    const tareasList = Object.keys(tareasData).map(myID => {
                        return {
                            ...tareasData[myID],
                            tareaID: myID
                        }
                    })

                    /*Tenemos que filtrar las tareas y que estén basadas de acuerdo al proyecto como tal*/
                    const tareasProyecto = tareasList.filter(tarea => tarea.idProyecto === proyectoID);

                    //Modificamos el valor de encargados para que tengan los datos del usuario encargado como tal
                    const tareasWithEncargados = await Promise.all(
                        tareasProyecto.map(async tarea => {
                            const encargadosInfo = await Promise.all(
                                tarea.encargados.map(async encargadoID => {
                                    return await fetchUserData(encargadoID);
                                })
                            )
                            return { ...tarea, encargadosInfo};
                        })
                    )

                    setTareas(tareasWithEncargados);
                    console.log("Lista de tareas del proyecto filtradas: ", tareasWithEncargados);
                }
            } catch (error) {
                console.error("Error consiguiendo la información del usuario: ", error);
            }
            
        }
        fetchTareas();
    }, 
    //Se usa el valor de proyectoID para las tareas, si éste cambia obviamente afectará acá
    [proyectoID])

    if (!usuarioID || !proyectoID || !usuarioActual){
        return <div>Cargando</div>
    }

    const handleCerrarSesion = () => {
        setUsuarioActual(null);
        navigate('/');
    };

    //Función para generar las estrellas 
    const renderPrioridadStars = (prioridad) => {
        if (prioridad >= 1 && prioridad <= 3){
            return "Alto  (" + prioridad + ")"
        } else if (prioridad >= 4 && prioridad <= 7){
            return "Medio  (" + prioridad + ")"
        } else {
            return "Bajo  (" + prioridad + ")"
        }
    }

    return (
        <section className="Plataforma">
            <section className='Tab'>
                <div>
                    <img src={logo} alt='Proyex logo'/>
                    <label>Bienvenido: {usuarioActual.nombre}</label>
                </div>
                <div>
                    <button onClick={handleCerrarSesion}>Cerrar Sesión</button>
                </div>
            </section>

            <section className='Element-Data'>
                <label className='Title'>Lista de Tareas</label>
                <div className='Section'>
                    {tareas.map(tarea => (
                        <div key={tarea.tareaID} className='Member'>
                            <label><strong>Tarea:</strong>   {tarea.nombre} </label>
                            <label><strong>Descripción:</strong> {tarea.descripcion}</label>
                            <label><strong>Prioridad: </strong> {renderPrioridadStars(tarea.prioridad)}</label>
                            <label><strong>Miembros: </strong></label>
                            <div className='Encargados'>
                                {tarea.encargadosInfo.map((encargado, index) => (
                                    <label key={index}>{encargado ? encargado.nombre : 'Usuario no encontrado'} </label>
                                ))}
                            </div>
                            <div className='FechaEntrega'>
                                <label><strong>Fecha de entrega: </strong></label>
                                <label>{tarea.fecha.split('T')[0]}</label>
                                <label style={{ marginLeft: '100px'}}><strong>Hora de entrega: </strong></label>
                                <label>{tarea.fecha.split('T')[1]}</label>
                            </div>
                            <label><strong>Estado: </strong>{tarea.estado}</label>
                        </div>
                    ))}
                </div>
            </section>
        </section>
    )
}
