import useStyles from "./styles"

import { Paper } from "@mui/material"

const AppFooter = () => {
  const classes = useStyles()

  const year = new Date().getFullYear()

  return (
    <Paper className={classes.footer__container} data-testid="footer-test">
      {`Copyright © ${year} HysterYale, all rights reserved.`}
    </Paper>
  )
}

export { AppFooter }
