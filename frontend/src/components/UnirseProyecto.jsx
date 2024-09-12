import React, { useState, useEffect } from 'react';
import './styles/UnirseProyecto.css';
import {useNavigate} from "react-router-dom";

/*Como tal, la plataforma, sea desde registro o desde iniciar sesión, siempre recibe una id de usuario, ésta
id nos servirá para sellar el usuario del sistema a usar, será el parámetro id de usuario*/
import { useParams } from 'react-router-dom';

//Base de datos por firebase
import app from "../firebaseConfig.js";
import { getDatabase, get, ref, update } from 'firebase/database';

export default function CrearProyecto() {
    const { usuarioID } = useParams();

    //Usamos el state para restaurar datos o información
    //Muy basado en Registrar.jsx, checar la documentación de éste
    const [buscarProyecto, setBuscarProyecto] = useState({
      codigo: ""
    })

    const [proyectos, setProyectos] = useState([]);

    const navigate = useNavigate();

    //Sacamos los datos de los usuarios
    useEffect(() => {
        const fetchProyectos = async() => {
            const db = getDatabase(app);
            const dbRef = ref(db, "proyectos");
            const snapshot = await get(dbRef);

            if (snapshot.exists){
                const myData = snapshot.val()

                const temporaryArray = Object.keys(myData).map(myID => {
                    return {
                        ...myData[myID],
                        codigoGrupo: myID
                    }
                });

                setProyectos(temporaryArray);
                console.log("Todos los proyectos en el sistema: ", temporaryArray);
            }else{
                console.log("No hay valores dentro de nuestra tabla de base de datos");
            }
        }

        fetchProyectos();
    }, [usuarioID]); //Incluimos éste caso como dependencia para recorrer el efecto si hay cambios


    //Función para establecer cambios
    const handleUserChange = (event) => {
      setBuscarProyecto(
        prev => ({...prev, [event.target.name]: event.target.value})
      );
    }


    const handleUnirseProyecto = async(event) => {
        event.preventDefault();
        try{
            if (!buscarProyecto.codigo) {
                alert('Por favor, complete todos los campos.');
                return;
            }

            console.log("Data que ha ingresado el usuario: ", buscarProyecto.codigo);

            //Pasamos check si el usuario digitó el correo igual que uno de los códigos del grupo
            const proyectoUnirse = proyectos.find(proyecto => proyecto.codigoGrupo === buscarProyecto.codigo);
            if (!proyectoUnirse){
                alert("No se encontró ningún con dicho código de proyecto, por favor intentélo con otro");
                return;
            }

            //Checamos si el usuario ya es un colaborador
            if (proyectoUnirse.colaboradores.includes(usuarioID)) {
                alert("¡Ya eres colaborador en éste proyecto!");
                return;
            }
            
            //En caso de encontrar uno de los casos entonces...
            const db = getDatabase(app);
            const proyectoRef = ref(db, "proyectos/"+proyectoUnirse.codigoGrupo);
            await update(proyectoRef, {
                colaboradores: [...proyectoUnirse.colaboradores, usuarioID]
            });

            alert(`El usuario ha sido agregado a proyecto: ${proyectoUnirse.nombre}`);
            navigate(`/Proyectos/${usuarioID}`);
        } catch (error) {
            console.error("Ha aparecido un error: ", error);
            alert("Error al guardar los datos");
        }
    }
    

    return (
      <section className="App-content">
        <form className='Form'>
          <div className='Elemento'>
            <label className='Titulo'>Unirse a grupo de proyecto</label>
          </div>

        {/*
          <div className='Elemento'>
            <label>Link de invitado</label>
            <input type={'url'} placeholder='https://link-to-join-group//group-in-term.com' name='link'
            onChange={handleUserChange}/>
          </div>
        */}

          <div className='Elemento'>
            <label>Código de grupo de proyecto</label>
            <input type={'text'} placeholder='-NuHuUnlilqNEUebVIFz' name='codigo'
            onChange={handleUserChange}/>
          </div>

          <div className='Elemento'>
            <button onClick={handleUnirseProyecto}>Unirse a Proyecto</button>
          </div>
        </form>
      </section>
    )
}
