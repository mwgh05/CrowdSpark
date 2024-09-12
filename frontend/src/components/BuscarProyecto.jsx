import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

import app from "../firebaseConfig.js";
import { getDatabase, ref, get } from 'firebase/database';

import { Link } from "react-router-dom";

import logo from '../images/icon.png';
import './styles/BuscadorProyecto.css';

export default function BuscarProyectoPorNombre() {
    const { usuarioID } = useParams();
    const [usuarioActual, setUsuarioActual] = useState(null);


    const [projects, setProjects] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState([]);

    useEffect(() => {
        //Traemos la información de dicho usuario específico
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

        fetchUsuario();
    }, [usuarioID]); //Incluimos éste caso como dependencia para recorrer el efecto si hay cambios

    useEffect(() => {
        const fetchProjects = async () => {
            try {
                const db = getDatabase(app);
                const projectsRef = ref(db, 'proyectos');
                const snapshot = await get(projectsRef);
                if (snapshot.exists()) {
                    const projectData = snapshot.val();

                    const projectList = Object.keys(projectData).map( myID => {
                        return {
                            ...projectData[myID],
                            id: myID
                        }
                    });
                    setProjects(projectList);
                    console.log("Todos los proyectos como tal: ", projectList);
                } else {
                    console.log('No projects found.');
                }
            } catch (error) {
                console.error('Error fetching projects:', error);
            }
        };

        fetchProjects();
    }, []);

    useEffect(() => {
        const results = projects.filter(project =>
            project.nombre.toLowerCase().includes(searchTerm.toLowerCase())
        );
        setSearchResults(results);
    }, [searchTerm, projects]);

    const handleSearch = event => {
        setSearchTerm(event.target.value);
    };

    if (!usuarioID || !usuarioActual){
        return <div>Cargando</div>
    }

    return (
        <section className="Plataforma">
            <section className='Tab'>
                <div>
                    <img src={logo} alt='Proyex logo'/>
                    <label>Bienvenido: {usuarioActual.nombre}</label>
                </div>
                <div>
                    {/*Creamos un botón con la función de cerrar sesión*/}
                    <button> Cerrar Sesión </button> {/*Cerrar sesión  Log out*/}
                </div>
            </section>
            <section className='Buscador'>
                <h2>Buscar Proyecto por Nombre</h2>
                <input
                    type="text"
                    placeholder="Nombre del proyecto"
                    value={searchTerm}
                    onChange={handleSearch}
                />
            </section>
            <section className='Mostrador'>
                <ul>
                    {searchResults.map(proyecto => (
                        <li key={proyecto.id}>
                            <Link to={`/Tareas/${usuarioID}/${proyecto.id}`}>
                                <div>
                                    <h3>{proyecto.nombre}</h3>
                                    <p>Fecha de Deadline: {proyecto.fecha}</p>
                                    <p><strong>Líder:</strong> {proyecto.lider}</p>
                                    <p><strong>Colaboradores:</strong> {proyecto.colaboradores.join(', ')}</p>
                                </div>  
                            </Link>
                        </li>
                    ))}
                </ul>
            </section>
            
        </section>
    );
}
