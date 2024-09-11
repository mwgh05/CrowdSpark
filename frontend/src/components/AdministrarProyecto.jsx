import React, { useState, useEffect } from 'react';

//Parte gráfica
import { DeleteForever, SupervisorAccount, Settings } from '@mui/icons-material'
import './styles/AdministrarProyecto.css';

import {useNavigate} from "react-router-dom";

/*Como tal, la plataforma, sea desde registro o desde iniciar sesión, siempre recibe una id de usuario, ésta
id nos servirá para sellar el usuario del sistema a usar, será el parámetro id de usuario*/
import { useParams } from 'react-router-dom';

//Base de datos por firebase
import app from "../firebaseConfig.js";
import { getDatabase, get, ref, set, update } from 'firebase/database';

export default function AdministrarProyecto() {
    const { usuarioID, proyectoID } = useParams();

    //Usamos el state para restaurar datos o información
    const [usuarioActual, setUsuarioActual] = useState(null);
    const [proyectoActual, setProyectoActual] = useState(null);

    //Todos los usuarios como tal y las tareas
    const [usuarios, setUsuarios] = useState([]);
    const [tareas, setTareas] = useState([]);

    //Muy basado en Registrar.jsx, checar la documentación de éste
    const [proyecto, setProyecto] = useState({
        //Inicialmente pasamos todos los datos directamente, si no es nulo, los datos estarán en el programa
        nombre: proyectoActual ? proyectoActual.nombre : "",
        fecha:  proyectoActual ? proyectoActual.fecha : "",
        tareas: tareas,
        colaboradores: usuarios,
        lider: proyectoID
    })

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

        const fetchProyecto = async() => {
            try{
                const db = getDatabase(app);
                const dbRef = ref(db, `proyectos/${proyectoID}`);
                const snapshot = await get(dbRef);

                if (snapshot.exists()){
                    const projectData = snapshot.val();
                    setProyectoActual(projectData);
                    
                    console.log("Dentro de crearTarea, si agarramos proyecto");
                }
            }catch(error){
                console.error("Error consiguiendo la información del usuario")
            }
        };

        fetchUsuario();
        fetchProyecto();
    }, [usuarioID, proyectoID]); //Incluimos éste caso como dependencia para recorrer el efecto si hay cambios

    //Método para filtrar los usuarios de
    useEffect(() => {
        //Primero, se depende del proyecto actual para continuar
        if (!proyectoActual) return; 

        const fetchAllUsersProyecto = async () => {
            //Tomamos la base de datos a la cual nos conectamos
            const db = getDatabase(app);

            //Referenciamos la base de datos, la ubicación donde guardamos los usuarios
            const dbRef = ref(db, "usuarios");

            //Buscamos información dentro de los casos, mediante un snapshot
            const snapshot = await get(dbRef);

            if (snapshot.exists()) {
                //Nos encargamos de filtrar todos los usuarios que estén dentro del proyecto como tal

                //Extraemos la información y las llaves como tal
                const usersData = snapshot.val();
                const usersList = Object.keys(usersData).map(myID => {
                    return {
                        ...usersData[myID],
                        usuarioID: myID
                    }
                })

                const colaboradores = proyectoActual.colaboradores || [];

                //Filtramos como tal
                const usersInProyecto = usersList.filter(usuario => colaboradores.includes(usuario.usuarioID))

                setUsuarios(usersInProyecto);
                console.log("Todos los datos del proyecto: ", usersInProyecto);
            } else{
                console.log("No hay valores dentro de nuestra tabla de base de datos");
            }
        };

        const fetchAllTareasProyecto = async () => {
            const db = getDatabase(app);
            const dbRef = ref(db, "tareas");
            const snapshot = await get(dbRef);

            if (snapshot.exists()) {
                const tareasData = snapshot.val();
                const tareasList = Object.keys(tareasData).map(myID => {
                    return {
                        ...tareasData[myID],
                        tareaID: myID
                    }
                });

                const tareasInProyecto = tareasList.filter(tarea => tarea.idProyecto === proyectoID);
                setTareas(tareasInProyecto);
                console.log("Todos las tareas del proyecto: ", tareasInProyecto);
            }
        }

        fetchAllUsersProyecto();
        fetchAllTareasProyecto();
    }, [proyectoActual, proyectoID])


    //Método para agarrar y actualizar el valor de proyecto inicial
    useEffect(() => {
        if (!proyectoActual || !tareas || !usuarios) return; 
        
        setProyecto(prevProyecto => ({
            ...prevProyecto,
            nombre: proyectoActual.nombre || "",
            fecha: proyectoActual.fecha || "",
            tareas: tareas,
            colaboradores: usuarios,
            lider: proyectoID
        }))
    }, [proyectoActual, tareas, usuarios, proyectoID])

    //Función para establecer cambios
    const handleUserChange = (event) => {
        setProyecto(
            prev => ({...prev, [event.target.name]: event.target.value})
        );

        console.log("Cambios aplicados como tal: ", proyecto)
    }

    const handleModificarTarea = async(event) => {
        event.preventDefault();
        try{
            const db = getDatabase(app);

            if (!proyecto.nombre || 
                !proyecto.fecha) {
                alert('Por favor, completa todos los campos.');
                return;
            }

            console.log("Tarea imprimir como tal: ", proyecto);

            //Creamos una carpeta o archivo basado en el string de posición
            const updProyectoRef = ref(db, `proyectos/${proyectoID}`);
            await update(updProyectoRef, {
                nombre: proyecto.nombre,
                fecha: proyecto.fecha
            })
            
            //Actualización se ha hecho de forma correcta
            alert("Se ha actualizado correctamente los datos del proyecto");
            navigate(`/Tareas/${usuarioID}/${proyectoID}`);
        } catch (error) {
            console.error("Ha aparecido un error: ", error);
            alert("Error al guardar los datos");
        }
    }
    
    const handleDeleteUser = async(event, id_usuario) => {
        event.preventDefault(); //Evitamos reinicio de pantalla

        if (window.confirm("¿Estás seguro que quieres quitar a este usuario del proyecto?")){
            try {
                const db = getDatabase(app);

                //Removemos la ID del usuario de la lista de colaboradores
                const colaboradoresActualizado = proyecto.colaboradores.filter(usuario=>
                    usuario.usuarioID !== id_usuario)

                //Checamos que el dato como tal sea o no un array
                console.log("Valor antes de convertir o checar en array: ", colaboradoresActualizado);

                //Ahora simplemente hacemos un mapeado para extraer las id de usuario como tal
                const updColaboradores = colaboradoresActualizado.map(usuario => usuario.usuarioID);
                
                console.log("Luego de extraer las id: ", updColaboradores);
                
                //Actualizamos el proyecto en la base de datos con la lista de colaboradores
                const proyectoActualizar = ref(db, `proyectos/${proyectoID}/colaboradores`);
                await set(proyectoActualizar, updColaboradores);

                //Actualizamos el estado de la nueva lista de colaboradores como tal
                setProyecto(prevProyecto => ({
                    ...prevProyecto,
                    colaboradores: colaboradoresActualizado
                }))

                alert("Se ha eliminado al usuario de la lista de colaboradores del proyecto");
            } catch(error){
                console.error("Ha aparecido un error: ", error);
            }
        } else {
            console.log("La eliminación del usuario ha sido cancelada")
        }
    }

    //Botones encargados de enviar el usuario a las tareas, o a modificar tarea como tal
    const handleTareaClick = async(event, tareaID) => {

    }
    const handleConfiguracionClick = async(event, tareaID) => {
        
    }

    if (!usuarioID || !proyectoID || !usuarioActual || !proyectoActual){
        return <div>Cargando</div>
    }

    return (
        <section className="App-content">
            <form className='Form'>
                <div className='Elemento'>
                    <label className='Titulo'>Administrar Proyecto</label>
                </div>

                <div className='Elemento'>
                    <label>Nombre</label>
                    <input type={'text'} placeholder='Aplicacion' name='nombre'
                    value={proyecto.nombre} onChange={handleUserChange}/>
                </div>

                <div className='Elemento'>
                    <label>Fecha y hora de entrega</label>
                    <input type={'datetime-local'} placeholder='2018-06-12T19:30' name='fecha'
                    value={proyecto.fecha} onChange={handleUserChange}/>
                </div>

                <div className='Elemento'>
                    <label>Lista de tareas</label>
                    <div className='Section'>
                        {proyecto.tareas.map(tarea => (
                            <div key={tarea.tareaID} className='Tareas'>
                                <span onClick={() => handleTareaClick(tarea.tareaID)}>
                                    {tarea.nombre}
                                </span>
                                <button onClick={() => handleConfiguracionClick(tarea.tareaID)}>
                                    <Settings/>
                                </button>
                            </div>
                        ))}
                    </div>
                </div>

                <div className='Elemento'>
                    <label>Lista de miembros</label>
                    <div className='Section'>
                        {proyecto.colaboradores.map(usuario => (
                            <div key={usuario.usuarioID} className='Member'>
                                <span>
                                    {usuario.nombre} 
                                    {usuario.usuarioID === usuarioID && <SupervisorAccount className='supervisor-icon'/>}</span>
                                {usuario.usuarioID !== usuarioID && (
                                    <button onClick={(event) => handleDeleteUser(event, usuario.usuarioID)}>
                                        <DeleteForever/>
                                    </button>
                                )}
                            </div>
                        ))}
                    </div>
                </div>

                <div className='Elemento'>
                    <button onClick={handleModificarTarea}>Modificar Proyecto</button>
                </div>
            </form>
        </section>
    )
}
