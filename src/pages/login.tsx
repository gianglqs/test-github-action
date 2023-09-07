import { styled, Paper } from "@mui/material";

import NextHead from "next/head";
// import Image from 'next/image'
import { LoadingButton } from "@mui/lab";
import authApi from "@/api/auth.api";
import { useRouter } from "next/router";
import nookies, { setCookie } from "nookies";
import { useForm } from "react-hook-form";
import FormControlledTextField from "@/components/FormController/TextField";
import axios from "axios";

const StyledContainer = styled("div")(() => ({
  height: `calc(100vh - ${25}px)`,
}));

const StyledFormContainer = styled(Paper)(({ theme }) => ({
  display: "flex",
  flexDirection: "column",
  alignItems: "center",
  maxWidth: 400,
  padding: theme.spacing(3),
}));

export default function LoginPage() {
  const router = useRouter();
  const loginForm = useForm<any>({
    // resolver: yupResolver(validationSchema),
    shouldUnregister: false,
    defaultValues: { user_id: "", password: "", grant_type: "" },
  });

  const handleSubmitLogin = loginForm.handleSubmit(async (formData: any) => {
    try {
      const transformData = {
        grant_type: "password",
        username: formData?.email,
        password: formData?.password,
      };

      const options = {
        method: "POST",
        headers: {
          "content-type": "application/x-www-form-urlencoded",
        },
        auth: {
          username: "client",
          password: "password",
        },
      };
      axios
        .post("http://192.168.1.155:8080/oauth/token", transformData, options)
        .then((response) => {
          const { redirect_to, access_token } = response.data;
          setCookie(null, "token", access_token, { maxAge: 2147483647 });
          setCookie(null, "redirect_to", redirect_to, { maxAge: 2147483647 });
          router.push(redirect_to);
        })
        .catch((error) => {});
    } catch (error) {
      alert("error");
    }
  });

  return (
    <>
      <NextHead>
        <title>HysterYale - Sign in</title>
      </NextHead>
      <StyledContainer className="center-element">
        {/* <CssBaseline /> */}
        <StyledFormContainer>
          <div id="logo" role="logo">
            Hyster-Yale
            {/* <Image src={logo as any} alt="The logo" width={160} height={40} /> */}
          </div>
          <form onSubmit={handleSubmitLogin}>
            <FormControlledTextField
              control={loginForm.control}
              sx={{ mt: 2 }}
              label="Email"
              name="email"
            />
            <FormControlledTextField
              control={loginForm.control}
              sx={{ mt: 2 }}
              label="Password"
              required
              name="password"
              autoComplete="current-password"
              type="password"
            />

            {/* <Link href={`/dashboard`}>
            Sign in
          </Link> */}
            <LoadingButton
              sx={{ mt: 2 }}
              loadingPosition="start"
              type="submit"
              fullWidth
              variant="contained"
              color="primary"
              // onClick={getAccessToken}
              // loading={loading}
              // disabled={loadingSSO || loading}
            >
              Sign in
            </LoadingButton>
          </form>
        </StyledFormContainer>
      </StyledContainer>
    </>
  );
}
