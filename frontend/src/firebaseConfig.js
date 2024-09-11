// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyDNMQJ-WGscZlMnWuU-GJZ6RGDjvN6lkuM",
  authDomain: "generadorproyectos-contr-c226a.firebaseapp.com",
  databaseURL: "https://generadorproyectos-contr-c226a-default-rtdb.firebaseio.com",
  projectId: "generadorproyectos-contr-c226a",
  storageBucket: "generadorproyectos-contr-c226a.appspot.com",
  messagingSenderId: "357860051105",
  appId: "1:357860051105:web:8c49f531492406da6648e0"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

export default app;