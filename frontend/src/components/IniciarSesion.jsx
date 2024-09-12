import React, { useState, useEffect } from 'react';
import './styles/IniciarSesion.css'; //Importamos las librerías
import { FaEye, FaEyeSlash } from 'react-icons/fa'

//Cambiar modos de contraseña o correo

//Importamos url y axios para uso de api  (Base de datos local)
//import axios from 'axios'; //Importamos el encargado de respuestas de api
//import { URL } from './data.js'

//Importamos el sistema a cambiar de página
import { useNavigate, Link } from "react-router-dom"

//Base de datos por firebase
import app from "../firebaseConfig.js";
import { getDatabase, ref, get } from "firebase/database"

export default function IniciarSesion() {
    //Muy basado en Registrar.jsx, checar la documentación de éste
    const [usuarioLogin, setUsuarioLogin] = useState({
      correo: "",
      password: ""
    })
    
    const [usuarios, setUsuarios] = useState([]);

    //Hacemos que se pueda mostrar o no
    const [showPassword, setShowPassword] = useState(false);


    const navigate = useNavigate();

    //Sacamos los datos de los usuarios
    useEffect(() => {
      const fetchAllUsers = async() => {
        //Tomamos la base de datos a la cual nos conectamos
        const db = getDatabase(app);

        //Referenciamos la base de datos, la ubicación donde guardamos los usuarios
        const dbRef = ref(db, "usuarios");

        //Buscamos información dentro de los casos, mediante un snapshot
        const snapshot = await get(dbRef);

        //Checar documentación de la siguiente modificación de datas en CambiarContraseña.jsx

        if (snapshot.exists()){
          const myData = snapshot.val();
          const temporaryArray = Object.keys(myData).map( myId => {
            return {
              ...myData[myId],
              usuarioID: myId
            }
          });

          setUsuarios(temporaryArray);
          console.log("Todos los datos de usuarios: ", temporaryArray);

          /*          Así lucía antes

          //Si el snapshot existe, entonces vamos a asignar los datos como tal

          setUsuarios(Object.values(snapshot.val()));
          console.log("Todos los datos de usuarios: ", Object.values(snapshot.val()));
          
          /*Inicialmente sin el snapshot.val, tenemos datos estilo json, luego con éste, tendríamos un 
          objeto de javascript, y finalmente con Object.values tendríamos un array de objetos JavaScript*/
        } else{
            console.log("No hay valores dentro de nuestra tabla de base de datos");
        }
      };
      fetchAllUsers();
    }, []);

    //Función para establecer cambios
    const handleUserChange = (event) => {
      setUsuarioLogin(
        prev => ({...prev, [event.target.name]: event.target.value})
      );
    }
    
    /*Función encargada de presionar botones
    const pressIniciarSesion = async(event) => {
      event.preventDefault(); //Evitamos reinicio de pantalla

      console.log('Presionamos el botón para encargarnos de iniciar sesión como tal');
      try {
        if (!usuarioLogin.correo || !usuarioLogin.password){
          alert('Por favor, completa todos los campos.');
          return;
        }

        const usuarioExists = usuarios.some((usuario) => 
          usuario.correo === usuarioLogin.correo && usuario.password === usuarioLogin.password
        )

        if (!usuarioExists){
          alert("El usuario no se encuentra en la base de datos, prueba a cambiar contraseña o correo");
          return;
        }

        navigate("/plataforma");
      }catch (error) {
        console.error("Ha aparecido un error: ", error);
      }
    }*/

    const handleIniciarSesion = async(event) => {
      event.preventDefault(); //Evitamos reinicio de pantalla
      try{
        //Validamos los campos que no estén vacíos
        if (!usuarioLogin.correo || !usuarioLogin.password){
          alert('Por favor, completa todos los campos.');
          return;
        }

        //Encontramos el usuario que cumpla igualdad de condiciones con correo y contraseña
        const usuario = usuarios.find((user) => 
          user.correo === usuarioLogin.correo && user.password === usuarioLogin.password
        )

        if (!usuario){
          alert("El usuario no se encuentra en la base de datos, prueba a cambiar contraseña o usar otro correo");
          return;
        }

        console.log("Valor antes de ser enviado a otra página: ", usuario.usuarioID);

        //Ahora navegamos a PlataformaGestión, no con el objeto usuario pero con la id para tomar dicho usuario
        navigate(`/plataforma/${usuario.usuarioID}`);
      }catch (error) {
        console.error("Ha aparecido un error: ", error);
      }
    }

    const togglePasswordVissibility = () => {
      setShowPassword(!showPassword);
    }

    return (
      <section className="App-content">
        <form className='Form'>
          <div className='Elemento'>
            <label className='Titulo'>Login</label>
          </div>

          <div className='Elemento'>
            <label>Correo Electrónico</label>
            <input type={'email'} placeholder='mauHernandez@gmail.com' name='correo'
            onChange={handleUserChange}/>
          </div>

          <div className='Elemento'>
            <label>Contraseña</label>
            <div className='password-input'>
              <input 
                type={showPassword ? 'text' : 'password'} 
                placeholder='**********' 
                name='password'
                onChange={handleUserChange}
              />
              {/*Activa ícono de visualización*/}
              {showPassword ? (
                <FaEyeSlash className='eye-icon' onClick={togglePasswordVissibility}/>
              ) : (
                <FaEye className='eye-icon' onClick={togglePasswordVissibility}/>
              )}
            </div>
            
          </div>

          <div className='Elemento'>
            <Link to="/contraseña">
              <a>¿Has olvidado tu correo electrónico?</a>
            </Link>
            <button onClick={handleIniciarSesion}>Iniciar Sesión</button>
          </div>
        </form>
      </section>
    )
}
