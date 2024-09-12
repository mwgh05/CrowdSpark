import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import app from "../firebaseConfig.js";
import { getDatabase, ref, get, onValue } from 'firebase/database';
import logo from '../images/icon.png';
import './styles/BuscadorProyecto.css';

export default function EnlistarProyecto() {
    const { usuarioID } = useParams();
    const [usuarioActual, setUsuarioActual] = useState(null);
    const [proyectos, setProyectos] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUsuario = async () => {
            try {
                const db = getDatabase(app);
                const dbRef = ref(db, `usuarios/${usuarioID}`);
                const snapshot = await get(dbRef);

                if (snapshot.exists()) {
                    const userData = snapshot.val();
                    setUsuarioActual(userData);
                } else {
                    console.log("No se encontró un usuario con dicha información")
                }
            } catch (error) {
                console.error("Error consiguiendo la información del usuario")
            }
        };

        fetchUsuario();

        const fetchProyectos = async () => {
            try {
                const db = getDatabase(app);
                const proyectosRef = ref(db, 'proyectos');
                onValue(proyectosRef, async (snapshot) => {
                    const data = snapshot.val();
                    if (data) {
                        const proyectosArray = Object.keys(data).map(key => ({ id: key, ...data[key] }));

                        proyectosArray.forEach(async (proyecto) => {
                            let db = getDatabase(app);
                            let lider = proyecto.lider;
                            let colaboradores = proyecto.colaboradores;
                            colaboradores.forEach(async (value, index) => {
                                let dbRef = ref(db, `usuarios/${value}`);
                                let snapshot = await get(dbRef);
                                let colaborador = await snapshot.val().nombre;
                                colaboradores[index] = colaborador;
                            })
                            let dbRef = ref(db, `usuarios/${lider}`);
                            let snapshot = await get(dbRef);
                            proyecto["lider"] = await snapshot.val().nombre;
                            setProyectos(proyectosArray);
                            setProyectos(proyectosArray);
                        });

                    } else {
                        setProyectos([]);
                    }
                    
                });
            } catch (error) {
                console.error("Error fetching projects:", error);
            }
        };

        fetchProyectos();

    }, [usuarioID]);

    const handleCerrarSesion = () => {
        setUsuarioActual(null);
        navigate('/');
    };

    useEffect(() => {
        const results = proyectos.filter(proyecto =>
            proyecto.nombre.toLowerCase().includes(searchTerm.toLowerCase())
        );
        setSearchResults(results);
    }, [searchTerm, proyectos]);

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
                    <button onClick={handleCerrarSesion}>Cerrar Sesión</button>
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
                                    <p>Fecha de Deadline: {new Date(proyecto.fecha).toLocaleString('es-CR')}</p>
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
