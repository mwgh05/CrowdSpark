import React, { useState, useEffect } from 'react';

//Parte gráfica
import './styles/CalendarioEventos.css';
import logo from '../images/icon.png';

//Importamos lo del calendario
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';

import {useNavigate} from "react-router-dom";

/*Como tal, la plataforma, sea desde registro o desde iniciar sesión, siempre recibe una id de usuario, ésta
id nos servirá para sellar el usuario del sistema a usar, será el parámetro id de usuario*/
import { useParams } from 'react-router-dom';

//Base de datos por firebase
import app from "../firebaseConfig.js";
import { getDatabase, get, ref } from 'firebase/database';

export default function CalendarioEventos() {
    const { usuarioID } = useParams();

    //Usamos el state para restaurar datos o información
    const [usuarioActual, setUsuarioActual] = useState(null);

    //Todos los proyectos y las tareas que le toca al usuario con base al usuarioID
    const [proyectosUsuario, setProyectosUsuario] = useState([]);
    const [tareasUsuario, setTareasUsuario] = useState([])

    //Proyectos elegidos del programa
    //const [selectedProyecto, setSelectedProyecto] = useState(null);
    const [valorPrioridad, setValorPrioridad] = useState(1);

    //const [showFilteredTasks, setShowFilteredTasks] = useState(false);

    const navigate = useNavigate();

    //TODO Datos como dependencias, prioridad, descripción, más para buscar en un futuro

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
    }, [usuarioID]); //Incluimos éste caso como dependencia para recorrer el efecto si hay cambios

    //Éste es para extraer todos los proyectos
    useEffect(() => {
        if (!usuarioActual) return;
        const fetchAllProyectosUsuario = async() => {
            const db = getDatabase(app);
            const dbRef = ref(db, "proyectos");
            const snapshot = await get(dbRef);

            if (snapshot.exists()) {
                const proyectData = snapshot.val();
                const proyectList = Object.keys(proyectData).map(myID => {
                    return {
                        ...proyectData[myID],
                        proyectoID: myID
                    }
                })

                /*Una vez tenemos toda la lista como tal, vamos a tomar el valor de colaboradores,
                y vamos a checar o filtrar solo todos los proyectos que tengan al usuarioID como colaborador*/

                console.log("Lista de proyectos sin filtrado: ", proyectList);

                const proyectosUsuario = proyectList.filter(proyecto => proyecto.colaboradores.includes(usuarioID));

                setProyectosUsuario(proyectosUsuario);
                console.log("Todos los proyectos del usuario: ", proyectosUsuario);
            }
        }
        fetchAllProyectosUsuario();
    }, [usuarioActual, usuarioID])

    const handleCerrarSesion = () => {
        setUsuarioActual(null);
        navigate('/');
    };
    
    
    
    //No se usa como tal, por ahora, entonces documentamos para arreglar en un futuro
    /*
    const filtrarDatos = async() => {
        //Filtramos las tareas como tal basadas en el nivel de prioridad
        const filteredTareas = tareasUsuario.filter(tarea => {
            //Checamos si la tarea como tal es de dicho proyecto
            const perteneceAProyecto = tarea.idProyecto === selectedProyecto.proyectoID;

            //Checamos si la prioridad de la tarea como tal es  igual al nivel de prioridad
            const checkPrioridad = parseInt(tarea.prioridad) === valorPrioridad;
            
            //Retornamos verdadero si se cumplen ambos casos
            return perteneceAProyecto && checkPrioridad;
        });
        //Actualizamos el state de como se filtran las tareas

        setTareasUsuario(filteredTareas);
        console.log("Todos las tareas filtradas basado en prioridad y proyecto: ", filteredTareas);
    }
    */

    useEffect(() => {
        if (!proyectosUsuario) return;
        /*Una vez extraídos todos los proyectos en los que se relaciona el usuario, vamos a extraer las tareas que
        se relacionen con dicho proyecto, y de ahí, extraemos las tareas relacionadas con dicho usuario*/
        const fetchAllTareasUsuario = async() =>{
            const db = getDatabase(app);

            //Inicializamos un array donde se guardan todos las tareas filtradas como tal
            let allTareas = []; 

            //Iteramos por cada uno de los proyectos, para buscar las tareas primeramente
            for (const proyecto of proyectosUsuario){
                const dbRef = ref(db, "tareas");
                const snapshot = await get(dbRef);

                if (snapshot.exists()) {
                    const tareasData = snapshot.val();

                    const tareas = Object.keys(tareasData).map(myID => {
                        return {
                            ...tareasData[myID],
                            tareaID: myID
                        }
                    })

                    //Filtramos la tarea con base al proyecto
                    const tareasProyecto = tareas.filter(tarea => tarea.idProyecto === proyecto.proyectoID);
                    console.log("Lista de tareas filtradas por proyecto: ", tareasProyecto);

                    //Filtramos las tareas donde el usuario es asignado como encargado
                    const tareasUsuario = tareasProyecto.filter(tarea => tarea.encargados.includes(usuarioID));

                    allTareas = allTareas.concat(tareasUsuario)
                }
            }

            setTareasUsuario(allTareas);
            console.log("Todos las tareas del usuario: ", allTareas);
        };
        fetchAllTareasUsuario();
    }, [proyectosUsuario, usuarioID])
    

    /*Creamos o iteramos las tareas como tal, y con base a éstas creamos los eventos como tal*/
    const events = tareasUsuario.map(tarea => ({
        //La fecha está en el formato "YYYY-MM-DDTHH:mm:ss"
        start: new Date(tarea.fecha), 
        end:   new Date(tarea.fecha),
        title: tarea.nombre
    }));

    if (!usuarioID || !usuarioActual || !proyectosUsuario || !tareasUsuario){
        return <div>Cargando</div>
    }

    //Función de control
    const handleProyectoChange = (event) => {
        //Primero que todo vamos a agarrar el objeto de búsqueda como tal, y setearlo como producto a buscar
        const proyectoID = event.target.value;
        const selected = proyectosUsuario.find(proyecto => proyecto.proyectoID === proyectoID);
        //setSelectedProyecto(selected);

        console.log("Proyecto seleccionado en lista: ", selected);
    };

    const handlePrioridadChange = (event) => {
        setValorPrioridad(parseInt(event.target.value));
    }

    // Custom render function for event content
    const renderEventContent = (eventInfo) => {
        return (
            <div>
                <b>{eventInfo.timeText}</b>
                <p>{eventInfo.event.title}</p>
            </div>
        );
    };

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

            <section className='Calendario'>
                <section className='input-container'>
                    <div className='select-container'>
                        <label>Buscar por Proyecto: </label>
                        <select onChange={handleProyectoChange}>
                            <option value="">Seleccione un Proyecto</option>
                            {proyectosUsuario.map(proyecto => (
                                <option key={proyecto.proyectoID} value={proyecto.proyectoID}>{proyecto.nombre}</option>
                            ))}
                        </select>
                    </div>

                    <div className='slider-container'>
                        <label>Prioridad: </label>
                        <input type="range" 
                            min="1"
                            max="10" 
                            value={valorPrioridad}
                            onChange={handlePrioridadChange} 
                            className="slider"/>
                        <span className='slider-value'>{valorPrioridad}</span>
                    </div>
                </section>
                
                <FullCalendar
                    plugins={[dayGridPlugin]}
                    initialView='dayGridMonth'
                    weekends={true} //Mostrar sábado y domingo
                    events={events}
                    eventContent={renderEventContent}
                />
            </section>
        </section>
    )
}
