import * as React from "react";
import { styled, createTheme, ThemeProvider } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";
import MuiDrawer from "@mui/material/Drawer";
import Box from "@mui/material/Box";
import MuiAppBar, { AppBarProps as MuiAppBarProps } from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import List from "@mui/material/List";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import IconButton from "@mui/material/IconButton";
import Badge from "@mui/material/Badge";
import Container from "@mui/material/Container";
import Grid from "@mui/material/Grid";
import Paper from "@mui/material/Paper";
import Link from "@mui/material/Link";
import MenuIcon from "@mui/icons-material/Menu";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import NotificationsIcon from "@mui/icons-material/Notifications";
import CreateIcon from "@mui/icons-material/AddCircle";
// import { mainListItems, secondaryListItems } from './listItems';
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import DashboardIcon from "@mui/icons-material/Dashboard";
import PeopleIcon from "@mui/icons-material/People";
import LayersIcon from "@mui/icons-material/Layers";
import { ReplayOutlined as ReloadIcon } from "@mui/icons-material";
import { Button, Menu } from "@mui/material";
// import SearchIcon from '@mui/icons-material/Search'
// import {
//     DataGridPro
//   } from '@mui/x-data-grid-pro'

import { AppSearchBar, DataTable } from "@/components";
// import { Cookies } from 'react-cookie';
import axios from "axios";
// import dashboardApi from '@/api/dashboard.api';

import nookies from "nookies";
import { useDispatch, useSelector } from "react-redux";
import dashboardSlice from "@/store/reducers/dashboard.reducer";
import { dashboardStore } from "@/store/reducers";
import { createAction } from "@reduxjs/toolkit";
import { useRouter } from "next/router";
import { DialogCreateUser } from "@/components/Dialog/Module/Dashboard/CreateDialog";

function Copyright(props: any) {
  return (
    <Typography
      variant="body2"
      color="text.secondary"
      align="center"
      {...props}
    >
      {`Copyright © ${new Date().getFullYear()} HysterYale, all rights reserved.`}
      {/* <Link color="inherit" href="https://mui.com/">
        Your Website
      </Link>{' '} */}
      {/* {new Date().getFullYear()} */}
    </Typography>
  );
}

const drawerWidth: number = 240;

interface AppBarProps extends MuiAppBarProps {
  open?: boolean;
}

const AppBar = styled(MuiAppBar, {
  shouldForwardProp: (prop) => prop !== "open",
})<AppBarProps>(({ theme, open }) => ({
  zIndex: theme.zIndex.drawer + 1,
  transition: theme.transitions.create(["width", "margin"], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  ...(open && {
    marginLeft: drawerWidth,
    width: `calc(100% - ${drawerWidth}px)`,
    transition: theme.transitions.create(["width", "margin"], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
  }),
}));

const Drawer = styled(MuiDrawer, {
  shouldForwardProp: (prop) => prop !== "open",
})(({ theme, open }) => ({
  "& .MuiDrawer-paper": {
    position: "relative",
    whiteSpace: "nowrap",
    width: drawerWidth,
    transition: theme.transitions.create("width", {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
    boxSizing: "border-box",
    ...(!open && {
      overflowX: "hidden",
      transition: theme.transitions.create("width", {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
      }),
      width: theme.spacing(7),
      [theme.breakpoints.up("sm")]: {
        width: theme.spacing(9),
      },
    }),
  },
}));

export default function Dashboard() {
  const [open, setOpen] = React.useState(true);
  createAction(`dashboard/GET_LIST`);
  const entityApp = "dashboard";
  const getListAction = React.useMemo(
    () => createAction(`${entityApp}/GET_LIST`),
    [entityApp]
  );
  const resetStateAction = React.useMemo(
    () => createAction(`${entityApp}/RESET_STATE`),
    [entityApp]
  );
  const router = useRouter();

  const dispatch = useDispatch();

  React.useEffect(() => {
    dispatch(getListAction());
  }, [getListAction, router.query]);

  React.useEffect(() => {
    return () => {
      dispatch(resetStateAction());
    };
  }, [router.pathname]);

  const toggleDrawer = () => {
    setOpen(!open);
  };

  const listUser = useSelector(dashboardStore.selectUserList);

  const defaultValue = {
    userName: "",
    password: "",
    email: "",
  };

  const [dialogCreateUser, setDialogCreateUser] = React.useState({
    open: false,
    detail: defaultValue as any,
  });

  const handleOpenCreateDialog = () => {
    setDialogCreateUser({
      open: true,
      detail: defaultValue,
    });
  };

  const handleCloseCreateDialog = () => {
    setDialogCreateUser({
      open: false,
      detail: defaultValue,
    });
  };

  const columns = [
    {
      field: "email",
      flex: 1,
      headerName: "Email",
    },
    {
      field: "role",
      flex: 1,
      headerName: "Role",
      renderCell(params) {
        return <span>{params.row.role.roleName}</span>;
      },
    },
    {
      field: "userName",
      flex: 1,
      headerName: "Name",
    },
    {
      field: "active",
      flex: 0.5,
      headerName: "Status",
      renderCell(params) {
        return (
          <Button
            variant="outlined"
            color={`${params.row.active ? "primary" : "error"}`}
          >
            Active
          </Button>
        );
      },
    },
    {
      field: "lastLogin",
      flex: 1.5,
      headerName: "Last Login",
    },
  ];

  return (
    <>
      <Box sx={{ display: "flex" }}>
        <AppBar position="absolute" open={open}>
          <Toolbar
            sx={{
              pr: "24px", // keep right padding when drawer closed
            }}
          >
            <IconButton
              edge="start"
              color="inherit"
              aria-label="open drawer"
              onClick={toggleDrawer}
              sx={{
                marginRight: "36px",
                ...(open && { display: "none" }),
              }}
            >
              <MenuIcon />
            </IconButton>
            <Typography
              component="h1"
              variant="h6"
              color="inherit"
              noWrap
              sx={{ flexGrow: 1 }}
            >
              {/* Dashboard */}
            </Typography>
            <IconButton color="inherit">
              <Badge color="secondary">
                <NotificationsIcon />
              </Badge>
            </IconButton>
          </Toolbar>
        </AppBar>
        <Drawer variant="permanent" open={open}>
          <Toolbar
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "flex-end",
              px: [1],
            }}
          >
            <IconButton onClick={toggleDrawer}>
              <ChevronLeftIcon />
            </IconButton>
          </Toolbar>
          <Divider />
          <List component="nav">
            <ListItemButton>
              <ListItemIcon>
                <DashboardIcon />
              </ListItemIcon>
              <ListItemText primary="Dashboard" />
            </ListItemButton>
            <ListItemButton>
              <ListItemIcon>
                <PeopleIcon />
              </ListItemIcon>
              <ListItemText primary="Users" />
            </ListItemButton>
            <Link href={`/booking`}>
              <ListItemButton>
                <ListItemIcon>
                  <LayersIcon />
                </ListItemIcon>
                <ListItemText primary="Financial Bookings" />
              </ListItemButton>
            </Link>
          </List>
        </Drawer>
        <Box
          component="main"
          sx={{
            backgroundColor: (theme) =>
              theme.palette.mode === "light"
                ? theme.palette.grey[100]
                : theme.palette.grey[900],
            flexGrow: 1,
            height: "100vh",
            overflow: "auto",
          }}
        >
          <Toolbar />
          <Grid container justifyContent="flex-end" sx={{ padding: 1 }}>
            <Button
              variant="outlined"
              style={{ marginLeft: 5 }}
              color="primary"
            >
              <ReloadIcon />
              Reload
            </Button>
            <Button
              onClick={handleOpenCreateDialog}
              variant="outlined"
              style={{ marginLeft: 5 }}
              color="primary"
            >
              <CreateIcon />
              New User
            </Button>
          </Grid>
          <Grid container sx={{ padding: 1.5 }}>
            <AppSearchBar></AppSearchBar>
          </Grid>
          <Paper elevation={1} sx={{ marginLeft: 1.5, marginRight: 1.5 }}>
            <Grid container>
              <DataTable
                hideFooter
                disableColumnMenu
                checkboxSelection
                tableHeight={770}
                rowHeight={70}
                rows={listUser}
                columns={columns}
                // selectionModel={selectedSpecList}
                // onSelectionModelChange={handleSelectSpecification}
                // getRowId={(params) => params.spec_id_raw}
              />
            </Grid>
          </Paper>
          <Copyright sx={{ padding: 2 }} />
        </Box>
      </Box>

      <DialogCreateUser
        {...dialogCreateUser}
        onClose={handleCloseCreateDialog}
      />
    </>
  );
}
