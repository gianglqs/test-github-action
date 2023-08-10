import React from "react";
import '../resources/logIn.css';
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Cookies from "universal-cookie";


const BASE_URL = "http://localhost:8080";
const queryString = require('querystring-es3'); 

function LogIn () {
    const navigate = useNavigate();
    var access_token = null;

    async function getAccessToken (){
    
        let endUrl = BASE_URL + "/oauth/token";

        // Configure body for Authorization and get Access Token
        const data = queryString.stringify({
            "grant_type": "password",
            "username": document.getElementById('inputEmail').value,
            "password": document.getElementById('inputPassword').value
        })


        // Configure headers and Basic Authentication
        const options = {
            method: 'POST',
            headers: {
                'content-type': 'application/x-www-form-urlencoded',  
            },
            auth: {
                username: 'client',
                password: 'password'
            }
        }

        axios.post(endUrl, data, options)
        .then(function(response) {
            console.log(response);
            access_token = response.data.access_token;
            const cookies = new Cookies();
            cookies.set("access_token", access_token);
            navigate("/homepage");
        }).catch(function(error) {
            console.log("Something went wrong");
            console.log(error);
        });
        return access_token;
    }

    return (
        
        <div className="logInBlock">

            <div className="navigationBlock">
                <img src="https://s21.q4cdn.com/775754248/files/images/logo.svg" alt="hyster-yale-logo"></img>
            </div>

            <div className='logInForm'>
                <div> 
                    <input id="inputEmail" 
                        className='inputEmail' 
                        placeholder='Email'>
                    </input>
                </div>
                <div>
                    <input id="inputPassword" 
                        className='inputPassword' 
                        placeholder='Password'
                        type="password">
                    </input>
                </div>
                <button className="buttonSignIn" onClick={getAccessToken}>Sign in</button>
            </div>
            
        </div>
    );
}


export default LogIn;

