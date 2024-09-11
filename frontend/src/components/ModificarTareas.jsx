import React, { useState, useEffect } from 'react';

//Parte gráfica
import './styles/ModificarTareas.css';
import Select from 'react-select'

import {useNavigate} from "react-router-dom";

/*Como tal, la plataforma, sea desde registro o desde iniciar sesión, siempre recibe una id de usuario, ésta
id nos servirá para sellar el usuario del sistema a usar, será el parámetro id de usuario*/
import { useParams } from 'react-router-dom';

//Base de datos por firebase
import app from "../firebaseConfig.js";
import { getDatabase, get, ref, update } from 'firebase/database';

export default function ModificarTareas() {
    const { usuarioID, proyectoID } = useParams();

    //Usamos el state para restaurar datos o información
    const [usuarioActual, setUsuarioActual] = useState(null);
    const [proyectoActual, setProyectoActual] = useState(null);

    const [tareas, setTareas] = useState([]);
    const [usuarios, setUsuarios] = useState([]);


    //Muy basado en Registrar.jsx, checar la documentación de éste
    const [nuevaTarea, setNuevaTarea] = useState({
        nombre:"",
        fecha:"",
        encargados: [],
        descripcion: "",
        idProyecto: "",
        prioridad: 1,
        estado: "",
        comentarios: [],
        tareaID: ""
    })

    const [formDisabled, setFormDisabled] = useState(true);

    const [comentariosText, setComentariosText] = useState('');

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
                }
            }catch(error){
                console.error("Error consiguiendo la información del usuario")
            }
        };

        fetchUsuario();
        fetchProyecto();
    }, [usuarioID, proyectoID]);

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
    }, [proyectoID])

    useEffect(() => {
        //Primero, se depende del proyecto actual para continuar
        if (!proyectoActual) return; 
        const fetchUsuarios = async() => {
            try{
                const db = getDatabase(app);
                const dbRef = ref(db, `usuarios`);
                const snapshot = await get(dbRef);

                if (snapshot.exists()){
                    const usuariosData = snapshot.val();
                    const usuariosList = Object.keys(usuariosData).map(myID => {
                        return {
                            ...usuariosData[myID],
                            usuarioID: myID
                        }
                    });

                    console.log("Lista de usuarios a checar: ", usuariosList);
                    //Filtramos como tal
                    const usuariosProyecto = usuariosList.filter(usuario => 
                        proyectoActual.colaboradores.includes(usuario.usuarioID))

                    console.log("Lista de usuarios a checar: ", usuariosList);

                    setUsuarios(usuariosProyecto);
                }
            } catch (error) {
                console.error("Error consiguiendo la información del usuario: ", error);
            }
        };

        fetchUsuarios();
    }, [proyectoActual])

    if (!usuarioID || !proyectoID || !usuarioActual || !proyectoActual || !tareas){
        return <div>Cargando</div>
    }

    //Función para elegir las tareas como tal
    const handleTareasChange = (event) => {
        //Primero que todo vamos a agarrar el objeto de búsqueda como tal, y setearlo como producto a buscar
        const tareaID = event.target.value;
        const selected = tareas.find(tarea => tarea.tareaID === tareaID);

        console.log("Proyecto seleccionado en lista: ", selected);

        //Lleno los valores de la nueva tarea a mostrar, claro, si está seleccionada como tal
        if (selected){

            const opcionEncargados = selected.encargados.map(encargadoID => {
                const usuario = usuarios.find(usuario => usuario.usuarioID === encargadoID);
                return usuario;
            });

            setNuevaTarea({
                nombre: selected.nombre,
                fecha: selected.fecha,
                encargados: opcionEncargados,
                descripcion: selected.descripcion,
                idProyecto: selected.idProyecto,
                prioridad: selected.prioridad,
                estado: selected.estado,
                comentarios: selected.comentarios,
                tareaID: selected.tareaID
            })

            setComentariosText('');

            if (selected.estado !== 'Incompleta'){
                //Si el valor no está desactivado como tal, entonces está permitido a modificar,
                //sino, todo se bloquea
                setFormDisabled(false);
            }
        } else {
            //Se desactiva sino es seleccionado
            setFormDisabled(true);
        }
    };

    const handleEncargadosChange = (selectedOptions) => {
        const newEncargados = selectedOptions ? selectedOptions.map(option => option.value) : [];
        console.log("Valor de encargados como tal: ", newEncargados);    
        setNuevaTarea(prev => 
            ({...prev,
                encargados: newEncargados}));
    }

    //Función para establecer cambios
    const handleUserChange = (event) => {
        setNuevaTarea(
            prev => ({...prev, [event.target.name]: event.target.value})
        );
    }

    //Hacemos como tal un arreglo a la hora de agregar comentarios de las tareas
    const handleAgregarComentario = (event) => {
        event.preventDefault();
        
        console.log(`Justo a la hora de introducir el texto a comentarios tenemos que: ${comentariosText} se puede iterar en ${nuevaTarea.comentarios}`)
        setNuevaTarea(prev => ({
            ...prev,
            comentarios: [...prev.comentarios, comentariosText]
        }));
        setComentariosText('');
    }

    const handleActualizarTareas = async(event) => {
        event.preventDefault();
        if (comentariosText !== ''){
            handleAgregarComentario(event);
        }

        try{
            const db = getDatabase(app);

            if (!nuevaTarea.nombre || 
                !nuevaTarea.encargados ||
                !nuevaTarea.fecha ||
                !nuevaTarea.descripcion) {
                alert('Por favor, completa todos los campos.');
                return;
            }

            console.log("Tarea a imprimir como tal: ", nuevaTarea);

            //Extraemos los valores como tal de las id de encargados
            const encargadosIDs = nuevaTarea.encargados.map(encargado => encargado.usuarioID);
            console.log("Lista de ids de encargados: ", nuevaTarea);

            const updTareaRef = ref(db, `tareas/${nuevaTarea.tareaID}`);
            await update(updTareaRef, {
                nombre: nuevaTarea.nombre,
                fecha: nuevaTarea.fecha,
                encargados: encargadosIDs,
                prioridad: nuevaTarea.prioridad,
                descripcion: nuevaTarea.descripcion,
                estado: nuevaTarea.estado,
                comentarios: nuevaTarea.comentarios
            })
            alert("Se han actualizado correctamente los datos de la tarea");
            navigate(`/Tareas/${usuarioID}/${proyectoID}`);
        } catch (error) {
            console.error("Ha aparecido un error: ", error);
            alert("Error al guardar los datos");
        }
    }

    const isLider = proyectoActual.lider === usuarioID;

    console.log("Proyecto actual a mostrar: ", proyectoActual)
    console.log("Usuarios totales a mostrar: ", usuarios)

    return (
        <section className="Plataforma">
            <form className='Form'>
                <div className='Elemento'>
                    <label className='Titulo'>Modificar Tareas en el Proyecto</label>
                </div>

                <div className='Elemento'>
                    <select onChange={handleTareasChange} className="buscarTareas">
                        <option value="">Seleccione una Tarea</option>
                        {tareas.map(tarea => (
                            <option key={tarea.tareaID} value={tarea.tareaID}>{tarea.nombre}</option>
                        ))}
                    </select>
                </div>

                <div className='Elemento'>
                    <label>Nombre</label>
                    <input type={'text'} placeholder='Crear un Login' name='nombre' value={nuevaTarea.nombre}
                    onChange={handleUserChange} disabled={(formDisabled === isLider)}/>
                </div>

                <div className='Elemento'>
                    <label>Fecha y hora de entrega</label>
                    <input type={'datetime-local'} placeholder='2018-06-12T19:30' name='fecha'
                    value={nuevaTarea.fecha} onChange={handleUserChange} disabled={(formDisabled === isLider)}/>
                </div>

                <div className='Elemento'>
                    <label>Encargados</label>
                    <Select
                        options={usuarios.map(encargado => ({
                            value: encargado.usuarioID,
                            label: encargado.nombre
                        }))}
                        isMulti
                        onChange={handleEncargadosChange}
                        name='encargados'
                        value={nuevaTarea.encargados ? nuevaTarea.encargados.map(encargado => ({
                            value: encargado.usuarioID,
                            label: encargado.nombre
                        })) : []}
                    />
                </div>
                <div className='Elemento'>
                    <label>Nivel de Prioridad de la Tarea</label>
                    <input type={'range'} min="1" max="10" name='prioridad' value={nuevaTarea.prioridad}
                    onChange={handleUserChange} disabled={(formDisabled === isLider)}/>
                    <span>{nuevaTarea.prioridad}</span>
                </div>

                <div className='Elemento'>
                    <label>Descripción completa de la Tarea</label>
                    <textarea name='descripción' 
                    placeholder='Los encargados como tal deben de empezar la función de...'
                    value={nuevaTarea.descripcion} onChange={handleUserChange}
                    disabled={(formDisabled === isLider)}/>
                </div>

                <div className='Elemento'>
                    <label>Estado</label>
                    <div>
                        <input type={'radio'} id="En Proceso" name='estado' value="En Proceso"
                        checked={nuevaTarea.estado === "En Proceso"} onChange={handleUserChange}
                        disabled={formDisabled}></input>
                        <label htmlFor='En Proceso'>En Proceso</label>
                    </div>
                    <div>
                        <input type={'radio'} id="Completado" name='estado' value="Completado"
                        checked={nuevaTarea.estado === "Completado"} onChange={handleUserChange}
                        disabled={formDisabled}></input>
                        <label htmlFor='Completado'>Completado</label>
                    </div>
                    <div>
                        <input type={'radio'} id="Incompleta" name='estado' value="Incompleta"
                        checked={nuevaTarea.estado === "Incompleta"} onChange={handleUserChange}
                        disabled={!formDisabled}></input>
                        <label htmlFor='Incompleta'>Incompleta</label>
                    </div>
                </div>

                <div className='Elemento'>
                    <label>Agregar Comentarios de la Tarea</label>
                    <textarea
                    placeholder='Durante el desarrollo de la tarea, se pudo experimentar ciertas problemáticas en...'
                    value={comentariosText}
                    disabled={(formDisabled)}
                    onChange={(event) => setComentariosText(event.target.value)}
                    />
                    <button className='AgregarComentarioButton'
                    onClick={handleAgregarComentario}> Agregar Comentario </button>
                </div>

                <div className='Elemento'>
                    <button onClick={handleActualizarTareas}> Modificar </button>
                </div>
            </form>
        </section>
    )
}
