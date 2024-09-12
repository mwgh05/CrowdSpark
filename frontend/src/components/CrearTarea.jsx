import React, { useState, useEffect } from 'react';
import './styles/CrearTarea.css';
import Select from 'react-select'

import {useNavigate} from "react-router-dom";

/*Como tal, la plataforma, sea desde registro o desde iniciar sesión, siempre recibe una id de usuario, ésta
id nos servirá para sellar el usuario del sistema a usar, será el parámetro id de usuario*/
import { useParams } from 'react-router-dom';

//Base de datos por firebase
import app from "../firebaseConfig.js";
import { getDatabase, get, ref, set, push } from 'firebase/database';

export default function CrearTarea() {
    const { usuarioID, proyectoID } = useParams();

    //Usamos el state para restaurar datos o información
    const [usuarioActual, setUsuarioActual] = useState(null);
    const [proyectoActual, setProyectoActual] = useState(null);

    const [usuarios, setUsuarios] = useState([]);

    //Muy basado en Registrar.jsx, checar la documentación de éste
    const [nuevaTarea, setNuevaTarea] = useState({
      nombre:"",
      fecha:"",
      encargados: [],
      descripcion: "",
      idProyecto: proyectoID,
      prioridad: 1,
      estado: "En Proceso",
      comentarios: []
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

    //Array de todos los usuarios como tal dentro del proyecto
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
        fetchAllUsersProyecto();
    }, [proyectoActual])


    //Función para establecer cambios
    const handleUserChange = (event) => {
        setNuevaTarea(
            prev => ({...prev, [event.target.name]: event.target.value})
        );
    }

    //Función para cambios en el select
    const handleEncargadosChange = (selectedOptions) => {
        const encargados = selectedOptions ? selectedOptions.map(option => option.value) : [];
        setNuevaTarea(prev => ({...prev, encargados}));
    }

    const handleCrearTarea = async(event) => {
        event.preventDefault();
        try{
            const db = getDatabase(app);

            if (!nuevaTarea.nombre || 
                !nuevaTarea.encargados ||
                !nuevaTarea.fecha ||
                !nuevaTarea.descripcion) {
                alert('Por favor, completa todos los campos.');
                return;
            }

            console.log("Tarea imprimir como tal: ", nuevaTarea);

            //Creamos una carpeta o archivo basado en el string de posición
            const newProyectoRef = push(ref(db, "tareas"));
            await set(newProyectoRef, {
                nombre: nuevaTarea.nombre,
                fecha: nuevaTarea.fecha,
                encargados: nuevaTarea.encargados,
                descripcion: nuevaTarea.descripcion,
                idProyecto: nuevaTarea.idProyecto,
                prioridad: parseInt(nuevaTarea.prioridad),
                estado: nuevaTarea.estado,
                comentarios: [nuevaTarea.descripcion]
            });
            alert("La Tarea ha sido guardada de forma exitosa");
            
            navigate(`/Tareas/${usuarioID}/${proyectoID}`);
        } catch (error) {
            console.error("Ha aparecido un error: ", error);
            alert("Error al guardar los datos");
        }
    }
    
    if (!usuarioID || !proyectoID || !usuarioActual || !proyectoActual){
        return <div>Cargando</div>
    }

    return (
        <section className="Plataforma">
            <form className='Form'>
                <div className='Elemento'>
                    <label className='Titulo'>Crear Tarea en el Proyecto</label>
                </div>

                <div className='Elemento'>
                    <label>Nombre</label>
                    <input type={'text'} placeholder='Crear un Login' name='nombre'
                    onChange={handleUserChange}/>
                </div>

                <div className='Elemento'>
                    <label>Fecha y hora de entrega</label>
                    <input type={'datetime-local'} placeholder='2018-06-12T19:30' name='fecha'
                    onChange={handleUserChange}/>
                </div>

                <div className='Elemento'>
                    <label>Encargados</label>
                    <Select
                        options={usuarios.map(usuario => ({ value: usuario.usuarioID, label: usuario.nombre}))}
                        isMulti
                        onChange={handleEncargadosChange}
                        name='encargados'
                    />
                </div>

                <div className='Elemento'>
                    <label>Nivel de Prioridad de la Tarea</label>
                    <input type={'range'} min="1" max="10" name='prioridad' onChange={handleUserChange} value={nuevaTarea.prioridad}/>
                    <span>{nuevaTarea.prioridad}</span>
                </div>

                <div className='Elemento'>
                    <label>Descripción completa de la Tarea</label>
                    <textarea name='descripcion' 
                    placeholder='Los encargados como tal deben de empezar la función de...'
                    onChange={handleUserChange}/>
                </div>

                <div className='Elemento'>
                    <button onClick={handleCrearTarea}>Crear</button>
                </div>
            </form>
        </section>
    )
}
