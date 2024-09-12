import React, { useState, useEffect } from 'react';
import './styles/Registrar.css'; //Importamos las librerías
import { FaEye, FaEyeSlash } from 'react-icons/fa'

//import axios from 'axios'; //Importamos el encargado de respuestas de api

//Importamos la URL inicial o base 
//import { URL } from './data.js'

//Importamos el sistema a cambiar de página
import { useNavigate } from "react-router-dom"

//Base de datos por firebase
import app from '../firebaseConfig'; 
import { getDatabase, ref, set, push, get } from "firebase/database"

//Cambiar modos de contraseña o correo

export default function Registrar() {
    /*Posición 0, el valor como tal, posición 1, la función de set a dicho valor, en éste caso los usaremos
    para los inputs como tal, para encargarnos de ejercer la función de iniciar sesión y registrarse*/
    
    /*Anteriormente yo habría visto un ejemplo de que se tenía que hacer una variable una por una, entonces
    haría una variable con useState para el nombre, correo y contraseña, sin embargo, todo ésto se puede
    reducir a una simple variable como objeto, aquí el ejemplo: (Checar handleUserChange)*/

    const [usuarioNuevo, setUsuarioNuevo] = useState({
        nombre: "",
        correo: "",
        password: "",
        admin: false
    });
    
    //Para pruebas, vamos a usar el useState para checar que los datos sean modificados o usados
    const [usuarios, setUsuarios] = useState([]);

    //Hacemos que se pueda mostrar o no
    const [showPassword, setShowPassword] = useState(false);

    //Creamos el método para navegar de página en página, útil luego de crear el usuario como tal
    const navigate = useNavigate();

    useEffect(() => {
        /*Al inicio del programa, nosotros vamos a encargarnos de usar la conexión con nuestra base de
        datos como tal, y usar los métodos al mismo tiempo de nuestra API*/

        const fetchAllUsers = async () => {
            /*Estamos usando un método de API, por eso mejor usar async, porque es asincrónico y hay que
            esperar a que se apliquen los cambios*/

            /*
            Inicialmente ésto era para registrar usuarios con base a una base de datos local, ahora
            usaremos una base de datos de tiempo real

            try {
                const res = await axios.get(URL + "/register");
                setUsuarios(res.data);
                console.log("Todos los datos de usuarios: ", res);
            }catch(error){
                console.error("Ha aparecido un error: ", error);
            }*/
            
            //Tomamos la base de datos a la cual nos conectamos
            const db = getDatabase(app);

            //Referenciamos la base de datos, la ubicación donde guardamos los usuarios
            const dbRef = ref(db, "usuarios");

            //Buscamos información dentro de los casos, mediante un snapshot
            const snapshot = await get(dbRef);

            if (snapshot.exists()) {
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
    }, [])

    const handleUserChange = (event) => {
        /*
        Anteriormente nosotros tendríamos 3 distintas variables y 3 funciones cada una llamando para modificar los
        cambios, pero en éste caso no es necesario, simplemente ocuparemos ésta función ser llamada en cada uno de los
        cambios de cualquier input relacionado

        const handleNombreChange = (event) => {
            setNombre(event.target.value);
        }
        */
        setUsuarioNuevo(
        /*Lo que hacemos es simple, pasamos el valor previo, de acuerdo a dicho valor previo vamos pasando cada uno
        de los datos modificados, basándonos en el nombre del input al cual tomar dicho valor, y como si fuera un json,
        a dicho objeto basado en nombre, le pasamos su valor*/
            prev=> ({...prev,
                [event.target.name]: event.target.value })    

            /*Ejemplo: 
            prev=> ({...prev,
                [event.target.name]: event.target.value })
                "nombre": "Mauricio Hernandez"
            */
        )
    }

    /*Función encargada de presionar botones Antigua
    const pressRegistrar = async(event) => {
        //Como default tenemos que el botón reinicia la pantalla, para evitar ésto podemos usar:
        event.preventDefault();
        try {
            console.log('Presionamos el botón para encargarnos de iniciar sesión como tal');
            //Primero que todo, vamos a validar que todas las entradas estén válidas
            if (!usuarioNuevo.nombre || 
                !usuarioNuevo.correo || 
                !usuarioNuevo.password) {
                alert('Por favor, completa todos los campos.');
                return;
            }

            Ahora, vamos a checar si el usuario es único, para ésto... lo principal es checar con base a el
            correo, si el correo ya existe en la base de datos, entonces avisamos que ya existe
            console.log("Correo creado: ", usuarioNuevo)
            const isCorreoUnico = usuarios.every((user) => 
                user.correo !== usuarioNuevo.correo
            );
            console.log("Checamos valor a ver que tal: ", isCorreoUnico);


            if (!isCorreoUnico){
                alert("El correo electrónico ya está en uso");
                return;
            }

            //Si los campos están completos, llamamos a axios normalmente, al método de post... 
            await axios.post(URL + "/register", usuarioNuevo);
            //Si no tira error, habremos creado nuestro usuario finalmente

            console.log("El usuario ha sido creado de forma exitosa")
            
            //TODO Crear una página inicial a la cual ir para el programa, y enviar el usuario acá
            navigate("/plataforma");
        } catch (error) {
            console.error("Ha aparecido un error: ", error);
        }
    }*/
    
    const registrarBaseDatos = async(event) => {
        event.preventDefault();
        try{
            const db = getDatabase();

            if (!usuarioNuevo.nombre || 
                !usuarioNuevo.correo || 
                !usuarioNuevo.password) {
                alert('Por favor, completa todos los campos.');
                return;
            }

            //Pasamos información del usuario nuevo al nodo de 'usuarios' en la Base de Datos
            const correoUnico = !usuarios.some(user => user.correo === usuarioNuevo.correo);
            if (!correoUnico){
                alert("Ya existe una cuenta en el sistema con el correo electrónico");
                return;
            }

            //Creamos una carpeta o archivo basado en el string de posición
            const newUsuarioRef = push(ref(db, "usuarios"));
            await set(newUsuarioRef, {
                nombre: usuarioNuevo.nombre,
                correo: usuarioNuevo.correo,
                password: usuarioNuevo.password,
                admin: usuarioNuevo.admin
            });
            //Extraemos la ID de la recién usada fila creada de usuario
            const newUsuarioID = newUsuarioRef.key;

            alert("La data ha sido guardada de forma exitosa");

            navigate(`/plataforma/${newUsuarioID}`);
        } catch (error) {
            console.error("Ha aparecido un error: ", error);
            alert("Error al guardar los datos");
        }
    }
    /*Si se completa, entonces simplemente mostramos los datos por ahora, acá haremos la función normal de
    base de datos*/

    const togglePasswordVissibility = () => {
        setShowPassword(!showPassword);
    }

    return (
      <section className="App-content">
        <form className='Form'>
            <div className='Elemento'>
                <label className='Titulo'>Registrar</label>
            </div>

            <div className='Elemento'>
                <label>Nombre</label>
                <input type={'text'} placeholder='Mauricio' name='nombre'
                onChange={handleUserChange}/>
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
                <button onClick={registrarBaseDatos}>Registrarse</button>
            </div>

        </form>
      </section>
    )
}
