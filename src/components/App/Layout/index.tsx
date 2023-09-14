import { AppBar, Grid, Typography } from "@mui/material"
import { useRouter } from "next/router"
import useStyles from "./styles"
import Head from "next/head"
import { AppFooter } from "../Footer"
import _ from "lodash"
import { useEffect, useMemo } from "react"
import { createAction } from "@reduxjs/toolkit"
import { useDispatch } from "react-redux"
import { AppLayoutProps } from "./type"

const AppLayout: React.FC<AppLayoutProps> = (props) => {
  const { children, entity } = props
  const classes = useStyles()

  const router = useRouter()
  const dispatch = useDispatch()

  const entityApp = useMemo(() => {
    return entity
  }, [entity])

  const getListAction = useMemo(
    () => createAction(`${entityApp}/GET_LIST`),
    [entityApp]
  )
  const resetStateAction = useMemo(
    () => createAction(`${entityApp}/RESET_STATE`),
    [entityApp]
  )

  useEffect(() => {
    dispatch(getListAction())
  }, [getListAction, router.query])

  useEffect(() => {
    return () => {
      dispatch(resetStateAction())
    }
  }, [router.pathname])

  const menuObj = {
    bookingOrder: "Financial Bookings",
    shipment: "Financial Shipments",
    analysis: "Margin Analysis",
    indicators: "Indicators",
    adjustment: "Adjustment of Cost Indicators",
    trend: "Trends",
    outliner: "Outliners",
    report: "Reports",
  }

  const renderMenu = () => {
    const otherOptions = _.keysIn(menuObj)
    return _.map(otherOptions, (name) => (
      <Typography
        variant="body1"
        fontWeight="fontWeightMedium"
        className={classes.label}
        color={router.pathname === `/${name}` ? "green" : ""}
      >
        {menuObj[name]}
      </Typography>
    ))
  }
  return (
    <>
      <Head>
        <title>{"Hyster -Yale - "}</title>
      </Head>
      <AppBar className={classes.header__container} position="static">
        <nav className={classes.navigation} role="nav">
          {renderMenu()}
        </nav>
      </AppBar>
      <Grid
        container
        style={{
          height: 910,
          width: "100%",
          maxHeight: 910,
        }}
      >
        <div className={classes.appLayout__container}>{children}</div>
      </Grid>
      <AppFooter className={classes.footer} />
    </>
  )
}

export { AppLayout }
