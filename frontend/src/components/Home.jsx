import React from 'react';
import './styles/Home.css'
import logo from '../images/icon.png'
import { Link } from 'react-router-dom'; //Importamos link del router react

export default function Home(){
    /*Ésto simplemente va a tener conexiones con la pantalla como tal, visualmente podremos ver todo
    lo necesario acerca del programa*/
    return (
        <section className='Home'>
            <section className='Opciones'>
                {/*Columna derecha, va en la opción de Login y Registrar*/}
                <div>
                    <img src={logo} alt='Proyex logo'/>
                    <label>Gestionador de Proyectos</label>
                </div>
                <div>
                    {/*Usamos los componentes de link en los botones*/}
                    <Link to='/iniciarSesion'>
                        <button> Iniciar Sesion </button> {/*Iniciar sesión  Log in*/}
                    </Link>
                    <Link to='/registrar'>
                        <button> Registrar </button> {/*Registrar  Sign Up*/}
                    </Link>
                </div>
            </section>
        </section>
    )
}
