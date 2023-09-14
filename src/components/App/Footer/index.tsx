import { Typography } from "@mui/material"

const AppFooter = (props: any) => {
  return (
    <Typography
      variant="body2"
      color="text.secondary"
      align="center"
      {...props}
    >
      {`Copyright © ${new Date().getFullYear()} HysterYale, all rights reserved.`}
    </Typography>
  )
}

export { AppFooter }
