import React from 'react';

import './App.css'; //Importamos las librerías

//Importamos las librerías por las cuales nos podemos trasladar como tal
import Home from './components/Home.jsx';
import IniciarSesion from './components/IniciarSesion.jsx';
import Registrar from './components/Registrar.jsx';
import CambiarContraseña from './components/CambiarContraseña'; 

import Proyectos from './components/Proyectos.jsx';
import CrearProyecto from './components/CrearProyecto.jsx';

import BuscarProyecto from './components/BuscarProyecto.jsx';
import EnlistarProyecto from './components/EnlistarProyecto.jsx';

import PlataformaGestion from './components/PlataformaGestion';
import UnirseProyecto from './components/UnirseProyecto';
import AdministrarProyecto from './components/AdministrarProyecto';

import Tareas from './components/Tareas';
import CrearTarea from './components/CrearTarea';
import EnlistarTareas from './components/EnlistarTareas';
import ModificarTareas from './components/ModificarTareas';

import Foro from './components/Foro.jsx'

import CalendarioEventos from './components/CalendarioEventos';

/*El useState como tal permite brindar la creación de funciones como tal, es similar a react-native, puesto
que es react por supuesto, useState en este caso se encarga de asignar estados, useEffect por otro lado se
encarga de llamar funciones al inicio de cargar la página*/

//Importamos lo indicado especial para cambiar de ubicación:
import {
  BrowserRouter,
  Routes,
  Route
} from "react-router-dom"

function App() {
  //App como tal simplemente debería de encargarse de la función o cambio de pantallas como tal
  return (
    <div className="App"> 
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home/>}/>
          <Route path="/registrar" element={<Registrar/>}/>
          {/*Actualizamos la dirección para recibir una id optional parámetro*/}
          <Route path="/iniciarSesion" element={<IniciarSesion/>}/>
          <Route path="/contraseña" element={<CambiarContraseña/>}/>
          

          {/*Cada que el usuario ingresa o se registra en el sistema, su info queda guardada y será usada
          a la hora del transcurso del programa*/}
          <Route path="/plataforma/:usuarioID" element={<PlataformaGestion/>}/>
          <Route path="/Proyectos/:usuarioID" element={<Proyectos/>}/>




          <Route path='/UnirseProyecto/:usuarioID' element={<UnirseProyecto/>}/>

          <Route path="/CrearProyecto/:usuarioID" element={<CrearProyecto/>}/>
          <Route path="/BuscarProyecto/:usuarioID" element={<BuscarProyecto/>}/>
          <Route path="/EnlistarProyecto/:usuarioID" element={<EnlistarProyecto/>}/>
          <Route path="/AdministrarProyecto/:usuarioID/:proyectoID" element={<AdministrarProyecto/>}/>

          <Route path="/Tareas/:usuarioID/:proyectoID" element={<Tareas/>}/>
          <Route path="/NuevaTarea/:usuarioID/:proyectoID" element={<CrearTarea/>}/>
          <Route path="/EnlistarTareas/:usuarioID/:proyectoID" element={<EnlistarTareas/>}/>
          <Route path="/ModificarTareas/:usuarioID/:proyectoID" element={<ModificarTareas/>}/>

          <Route path="/Foro/:usuarioID/:proyectoID" element={<Foro/>}/>

          <Route path="/Calendario/:usuarioID" element={<CalendarioEventos/>}/>
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
