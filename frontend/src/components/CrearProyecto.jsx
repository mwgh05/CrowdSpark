import React, { useState } from 'react';
import './styles/PlataformaGestion.css';
import {useNavigate} from "react-router-dom";

/*Como tal, la plataforma, sea desde registro o desde iniciar sesión, siempre recibe una id de usuario, ésta
id nos servirá para sellar el usuario del sistema a usar, será el parámetro id de usuario*/
import { useParams } from 'react-router-dom';

//Base de datos por firebase
import app from "../firebaseConfig.js";
import { getDatabase, get, ref, set, push } from 'firebase/database';

export default function CrearProyecto() {
    const { usuarioID } = useParams();

    //Muy basado en Registrar.jsx, checar la documentación de éste
    const [nuevoProyecto, setNuevoProyecto] = useState({
      nombre:"",
      fecha:"",
      tareas:[],
      colaboradores:[usuarioID],
      lider:usuarioID
    })

    const navigate = useNavigate();
    
    //Función para establecer cambios
    const handleUserChange = (event) => {
      setNuevoProyecto(
        prev => ({...prev, [event.target.name]: event.target.value})
      );
    }

    const handleCrearProyecto = async(event) => {
        event.preventDefault();
        try{
            const db = getDatabase(app);

            if (!nuevoProyecto.nombre || 
                !nuevoProyecto.fecha) {
                alert('Por favor, completa todos los campos.');
                return;
            }

            //Creamos una carpeta o archivo basado en el string de posición
            const newProyectoRef = push(ref(db, "proyectos"));
            await set(newProyectoRef, {
                nombre: nuevoProyecto.nombre,
                fecha: nuevoProyecto.fecha,
                tareas: nuevoProyecto.tareas,
                colaboradores: nuevoProyecto.colaboradores,
                lider: nuevoProyecto.lider,
                codigoGrupo: newProyectoRef.key
            });
            //Extraemos la ID de la recién usada fila creada de proyecto
            const newProyectoID = newProyectoRef.key;

            alert("La data ha sido guardada de forma exitosa");

            const snapshot = await get(ref(db, `proyectos/${newProyectoID}`));
            if(snapshot.exists){
                console.log("ID: ", newProyectoID);
                console.log("Proyecto: ", snapshot.val());
            }
            
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
            <label className='Titulo'>Crear Proyecto</label>
          </div>

          <div className='Elemento'>
            <label>Nombre</label>
            <input type={'text'} placeholder='Aplicacion' name='nombre'
            onChange={handleUserChange}/>
          </div>

          <div className='Elemento'>
            <label>Fecha y hora de entrega</label>
            <input type={'datetime-local'} placeholder='2018-06-12T19:30' name='fecha'
            onChange={handleUserChange}/>
          </div>


          <div className='Elemento'>
            <button onClick={handleCrearProyecto}>Crear</button>
          </div>
        </form>
      </section>
    )
}
