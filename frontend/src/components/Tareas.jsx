import React, { useState, useEffect } from 'react';
import './styles/PlataformaGestion.css';
import logo from '../images/icon.png';
import { useNavigate, Link } from "react-router-dom"

/*Como tal, la plataforma, sea desde registro o desde iniciar sesión, siempre recibe una id de usuario, ésta
id nos servirá para sellar el usuario del sistema a usar, será el parámetro id de usuario*/
import { useParams } from 'react-router-dom';

//Base de datos por firebase
import app from "../firebaseConfig.js";
import { getDatabase, get, ref } from 'firebase/database';

export default function Tareas() {
    //Accesamos a la id del usuario desde el parámetro url
    const { usuarioID, proyectoID } = useParams();

    //Usamos el state para restaurar datos o información
    const [usuarioActual, setUsuarioActual] = useState(null);
    const [proyectoActual, setProyectoActual] = useState(null);

    const navigate = useNavigate();

    console.log("Parametro recibido: ", usuarioID);
    
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
    }, [usuarioID, proyectoID]); //Incluimos éste caso como dependencia para recorrer el efecto si hay cambios

    if (!usuarioID || !proyectoID || !usuarioActual || !proyectoActual){
        return <div>Cargando</div>
    }

    const handleCerrarSesion = () => {
        setUsuarioActual(null);
        navigate('/');
    };

    const isLider = proyectoActual.lider === usuarioID;

    console.log("Entrando al proyecto a agregar tareas");
    console.log("Información del usuario actual: ", usuarioActual);
    console.log("Información del proyecto actual: ", proyectoActual);
    console.log(`Parámetros recibidos: ${usuarioID}   -   ${proyectoID}`);

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
            <section className='Menu'>
                <div className='Elemento'>
                    <label className='Titulo'>Gestión de Tareas</label>
                </div>
                
                <div className='Secciones'>
                    <div className='Elemento'>
                        {/*Si el usuario es líder, entonces mostramos el botón como tal*/}
                        {isLider && 
                            <Link to={`/NuevaTarea/${usuarioID}/${proyectoID}`}>
                                <button>Agregar Tareas</button>
                            </Link>
                        }
                    </div>
                    <div className='Elemento'>
                        <Link to={`/EnlistarTareas/${usuarioID}/${proyectoID}`}>
                            <button>Enlistar Tareas</button>
                        </Link>
                    </div>
                    <div className='Elemento'>
                        <Link to={`/ModificarTareas/${usuarioID}/${proyectoID}`}>
                            <button>Completar o actualizar tareas</button>
                        </Link>
                    </div>
                    <div className='Elemento'>
                        <Link to={`/Foro/${usuarioID}/${proyectoID}`}>
                            <button>Foro</button>
                        </Link>
                    </div>
                    
                    <div className='Elemento'>
                        {/*Si el usuario es líder, entonces mostramos el botón como tal*/}
                        {isLider && 
                            <Link to={`/AdministrarProyecto/${usuarioID}/${proyectoID}`}>
                                <button>Administrar el Proyecto</button>
                            </Link>
                        }
                    </div>

                </div>
            </section>
        </section>
    )
}
