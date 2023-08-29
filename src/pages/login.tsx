import { CssBaseline, styled,Paper,TextField } from '@mui/material';

import {  AppTextField } from '@/components'
import NextHead from 'next/head'
import Image from 'next/image'
import { LoadingButton } from '@mui/lab';
import Link from "next/link";

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

export default function LoginPage() {
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
          sx={{ mt: 2}}
          label="User Name"
          name="drawing_id"
        />
        <AppTextField
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