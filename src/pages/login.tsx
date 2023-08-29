import { CssBaseline, styled,Paper,TextField } from '@mui/material';

import {  AppTextField } from '@/components'
import NextHead from 'next/head'
import Image from 'next/image'
import { LoadingButton } from '@mui/lab';
import Link from "next/link";
import { useCookies } from 'react-cookie';
import axios from 'axios';


// import logo from '../public/hyster-yale-logo.gif'


const StyledContainer = styled('div')(() => ({
    height: `calc(100vh - ${25}px)`
  }))

const StyledFormContainer = styled(Paper)(({ theme }) => ({
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    maxWidth: 400,
    padding: theme.spacing(3)
  }))

const queryString = require('querystring-es3');

export default function LoginPage() {

  const [cookies, setCookie, removeCookie] = useCookies(['accessToken']);

  const getAccessToken = () => {
    // End destination for Authorization
    const BASE_URL = "http://192.168.1.154:8080";
    const endUrl = `${BASE_URL  }/oauth/token`;
    let accessToken;

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
    .then(response => {
        console.log(response);
        accessToken = response.data.access_token;
        setCookie('accessToken', accessToken);
        setCookie('authInformation', response.data)
    }).catch(error => {
        console.log("Something went wrong");
        console.log(error);
    });
  }

  return (
    <>
      <NextHead>
        <title>HysterYale - Sign in</title>
      </NextHead>
      <StyledContainer className="center-element">
      {/* <CssBaseline /> */}
      <StyledFormContainer>
        <div id="logo" role="logo">
            Hyster-Yale{/* <Image src={logo as any} alt="The logo" width={160} height={40} /> */}
        </div>
        <AppTextField
          id='inputEmail'
          sx={{ mt: 2}}
          label="Email"
          name="drawing_id"
        />
        <AppTextField
          id='inputPassword'
          sx={{ mt: 2 }}
          label="Password"
          name="drawing_id"
          required
        />

        {/* <Link href={`/dashboard`}>
          Sign in
        </Link> */}
        <Link href={`/dashboard`}>
          <LoadingButton
            sx={{ mt: 2 }}
            loadingPosition="start"
            type="submit"
            fullWidth
            variant="contained"
            color="primary"
            onClick={getAccessToken}
            // loading={loading}
            // disabled={loadingSSO || loading}
          >
            Sign in
          </LoadingButton>
        </Link>
      </StyledFormContainer>
      </StyledContainer>
    </>
  );
}