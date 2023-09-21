import { styled, Paper } from "@mui/material"

import NextHead from "next/head"
import { LoadingButton } from "@mui/lab"
import authApi from "@/api/auth.api"
import { useForm } from "react-hook-form"
import FormControlledTextField from "@/components/FormController/TextField"
import { useDispatch } from "react-redux"
import { commonStore } from "@/store/reducers"
import * as yup from "yup"
import { yupResolver } from "@hookform/resolvers/yup"
import { AppFooter } from "@/components/App/Footer"

const StyledContainer = styled("div")(() => ({
  height: `calc(100vh - ${25}px)`,
}))

const StyledFormContainer = styled(Paper)(({ theme }) => ({
  display: "flex",
  flexDirection: "column",
  alignItems: "center",
  maxWidth: 400,
  padding: theme.spacing(3),
}))

export default function LoginPage() {
  const validationSchema = yup.object({
    email: yup.string().required("Email is required"),
  })

  const resetPasswordForm = useForm({
    resolver: yupResolver(validationSchema),
    // shouldUnregister: false,
  })

  const dispatch = useDispatch()

  const handleResetPassword = resetPasswordForm.handleSubmit(
    async (formData: any) => {
      try {
        const transformData = {
          email: formData?.email,
        }
        await authApi.resetPassword(transformData)
        dispatch(
          commonStore.actions.setSuccessMessage("Reset Password Successfully")
        )
      } catch (error) {
        dispatch(commonStore.actions.setErrorMessage(error.message))
      }
    }
  )

  return (
    <>
      <NextHead>
        <title>HysterYale - Sign in</title>
      </NextHead>
      <StyledContainer className="center-element">
        <StyledFormContainer>
          <div id="logo" role="logo">
            Hyster-Yale
            {/* <Image src={logo as any} alt="The logo" width={160} height={40} /> */}
          </div>
          <form onSubmit={handleResetPassword}>
            <FormControlledTextField
              control={resetPasswordForm.control}
              sx={{ mt: 2 }}
              label="Email"
              name="email"
            />

            <LoadingButton
              sx={{ mt: 2 }}
              loadingPosition="start"
              type="submit"
              fullWidth
              variant="contained"
              color="primary"
            >
              Reset Password
            </LoadingButton>
          </form>
        </StyledFormContainer>
      </StyledContainer>
      <AppFooter />
    </>
  )
}
