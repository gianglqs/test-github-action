import * as React from 'react';
import { styled, createTheme, ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import MuiDrawer from '@mui/material/Drawer';
import Box from '@mui/material/Box';
import MuiAppBar, { AppBarProps as MuiAppBarProps } from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import List from '@mui/material/List';
import Typography from '@mui/material/Typography';
import Divider from '@mui/material/Divider';
import IconButton from '@mui/material/IconButton';
import Badge from '@mui/material/Badge';
import Container from '@mui/material/Container';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import Link from '@mui/material/Link';
import MenuIcon from '@mui/icons-material/Menu';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import NotificationsIcon from '@mui/icons-material/Notifications';
import CreateIcon from '@mui/icons-material/AddCircle';
// import { mainListItems, secondaryListItems } from './listItems';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import DashboardIcon from '@mui/icons-material/Dashboard';
import PeopleIcon from '@mui/icons-material/People';
import LayersIcon from '@mui/icons-material/Layers';
import { ReplayOutlined as ReloadIcon } from '@mui/icons-material'
import { Autocomplete, Button, TextField } from '@mui/material';
// import SearchIcon from '@mui/icons-material/Search'
// import {
//     DataGridPro
//   } from '@mui/x-data-grid-pro'

import {
    AppSearchBar,
    DataTable
  } from '@/components'
// import { Cookies } from 'react-cookie';
import axios from 'axios';
// import dashboardApi from '@/api/dashboard.api';

import nookies from 'nookies'
import { useDispatch,useSelector } from 'react-redux'
import dashboardSlice from '@/store/reducers/dashboard.reducer';
import { dashboardStore } from '@/store/reducers';
import { DateTimePicker } from '@mui/lab';
// import { createAsyncThunk } from '@reduxjs/toolkit';
// import { getData } from '@/store/reducers/dashboard.reducer';
// import { parse } from 'path';
  
// import Chart from './Chart';
// import Deposits from './Deposits';
// import Orders from './Orders';

function Copyright(props: any) {
  return (
    <Typography variant="body2" color="text.secondary" align="center" {...props}>
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
  shouldForwardProp: (prop) => prop !== 'open',
})<AppBarProps>(({ theme, open }) => ({
  zIndex: theme.zIndex.drawer + 1,
  transition: theme.transitions.create(['width', 'margin'], {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  ...(open && {
    marginLeft: drawerWidth,
    width: `calc(100% - ${drawerWidth}px)`,
    transition: theme.transitions.create(['width', 'margin'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen,
    }),
  }),
}));

const Drawer = styled(MuiDrawer, { shouldForwardProp: (prop) => prop !== 'open' })(
  ({ theme, open }) => ({
    '& .MuiDrawer-paper': {
      position: 'relative',
      whiteSpace: 'nowrap',
      width: drawerWidth,
      transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.enteringScreen,
      }),
      boxSizing: 'border-box',
      ...(!open && {
        overflowX: 'hidden',
        transition: theme.transitions.create('width', {
          easing: theme.transitions.easing.sharp,
          duration: theme.transitions.duration.leavingScreen,
        }),
        width: theme.spacing(7),
        [theme.breakpoints.up('sm')]: {
          width: theme.spacing(9),
        },
      }),
    },
  }),
);

// TODO remove, this demo shouldn't need to reset the theme.
const defaultTheme = createTheme();

export default function Booking() {
  const [open, setOpen] = React.useState(true);

  const dispatch = useDispatch()

  const toggleDrawer = () => {
    setOpen(!open);
  };

  const listUser = useSelector(dashboardStore.selectUserList)

  const [booking,setBooking] = React.useState([])

  console.log(booking);
  
  

  React.useEffect(() => {
    const cookies = nookies.get()
    const headers = { headers: {"Authorization": `Bearer${cookies.token}`} };
    axios.get("http://192.168.1.154:8080/bookingOrder/getAll", headers)
    .then(response => {
      setBooking(response.data.bookingOrderList)
    }).catch(error => {
    });
    
  }, []);


  const columns = [
    {
        field: 'orderNo',
        flex: 0.8,
        headerName: 'Order #'
    },
    {
      field: 'region',
      flex: 0.8,
      headerName: 'Region'
    },
    {
      field: 'ctryCode',
      flex: 0.8,
      headerName: 'Country'
    },
    {
      field: 'dealerName',
      flex: 0.8,
      headerName: 'Deale Name'
    },
    {
      field: 'Plant',
      flex: 0.8,
      headerName: 'Plant'
    }, 
    {
      field: 'truckClass',
      flex: 0.8,
      headerName: 'Class'
    },
    {
      field: 'series',
      flex: 0.8,
      headerName: 'Series'
    },
    {
      field: 'model',
      flex: 0.8,
      headerName: 'Models'
    },
    {
      field: 'qty',
      flex: 0.8,
      headerName: 'Qty'
    },
    {
      field: 'Total Cost',
      flex: 0.8,
      headerName: 'Total Cost'
    },
    {
      field: 'DN',
      flex: 0.8,
      headerName: 'DN'
    },
    {
      field: 'DN After Surcharge',
      flex: 0.8,
      headerName: 'DN After Surcharge'
    },
    {
      field: 'Margin $ After Surcharge',
      flex: 1,
      headerName: 'Margin $ After Surcharge'
    },
    {
      field: 'Margin % After Surcharge',
      flex: 1,
      headerName: 'Margin % After Surcharge'
    }

    // {
    //     field: 'role',
    //     flex: 1,
    //     headerName: 'Role',
    //     renderCell(params) {
    //       return <span>{params.row.role.roleName}</span>
    //     }
    // },
    // {
    //     field: 'userName',
    //     flex: 1,
    //     headerName: 'Name'
    // },
    // {
    //     field: 'active',
    //     flex: 0.5,
    //     headerName: 'Status',
    //     renderCell(params) {
    //       return <Button variant="outlined" color={`${params.row.active ? "primary": "error"}`}>Active</Button>
    //     }
    // },
    // {
    //     field: 'lastLogin',
    //     flex: 1.5,
    //     headerName: 'Last Login'
    // },
  ];

  return (
    <ThemeProvider theme={defaultTheme}>
      <Box sx={{ display: 'flex',marginTop: -8 }}>
        {/* <CssBaseline /> */}
        {/* <Drawer variant="permanent" open={open}>
          <Toolbar
            sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'flex-end',
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
            <ListItemButton>
                <ListItemIcon>
                    <LayersIcon />
                </ListItemIcon>
                <ListItemText primary="System Settings" />
            </ListItemButton>
          </List> 
        </Drawer> */}
        <Box
          component="main"
          sx={{
            backgroundColor: (theme) =>
              theme.palette.mode === 'light'
                ? theme.palette.grey[100]
                : theme.palette.grey[900],
            flexGrow: 1,
            height: '100vh',
            overflow: 'auto'
          }}
        >
          <Toolbar />
            <Grid container sx={{ padding: 1 }}><Link href={`/dashboard`}>Financial Bookings</Link></Grid>
            {/* <Grid container justifyContent='flex-end' sx={{ padding: 1 }}>
                <IconButton color="default">
                    <ReloadIcon />
                    Reload
                </IconButton>
                <IconButton color="default">
                    <CreateIcon />
                    Create
                </IconButton>
                sdf
            </Grid> */}
            <Grid container sx={{ padding:1 }}>
              <Autocomplete
                disablePortal
                id="combo-box-demo"
                options={[]}
                size='small'
                sx={{ width: 150 }}
                renderInput={(params) => <TextField {...params} label="Order #" />}
              />
              <Autocomplete
                disablePortal
                id="combo-box-demo"
                options={[]}
                size='small'
                sx={{ width: 150, marginLeft: 2 }}
                renderInput={(params) => <TextField {...params} label="Plant" />}
              />
              <Autocomplete
                disablePortal
                id="combo-box-demo"
                options={[]}
                size='small'
                sx={{ width: 150, marginLeft: 2 }}
                renderInput={(params) => <TextField {...params} label="Metaseries" />}
              />
              <Autocomplete
                disablePortal
                id="combo-box-demo"
                options={[]}
                size='small'
                sx={{ width: 150, marginLeft: 2 }}
                renderInput={(params) => <TextField {...params} label="Class" />}
              />
              <Autocomplete
                disablePortal
                id="combo-box-demo"
                options={[]}
                size='small'
                sx={{ width: 150, marginLeft: 2 }}
                renderInput={(params) => <TextField {...params} label="Model" />}
              />
              <Autocomplete
                disablePortal
                id="combo-box-demo"
                options={[]}
                size='small'
                sx={{ width: 150, marginLeft: 2 }}
                renderInput={(params) => <TextField {...params} label="Segment" />}
              />
              <Autocomplete
                disablePortal
                id="combo-box-demo"
                options={[]}
                size='small'
                sx={{ width: 150, marginLeft: 2 }}
                renderInput={(params) => <TextField {...params} label="AOP Margin %" />}
              />
              <Autocomplete
                disablePortal
                id="combo-box-demo"
                options={[]}
                size='small'
                sx={{ width: 150, marginLeft: 2 }}
                renderInput={(params) => <TextField {...params} label="Margin %" />}
              />
            </Grid> 
            <Grid container sx={{ padding:1 }}>
              <Autocomplete
                  disablePortal
                  id="combo-box-demo"
                  options={[]}
                  size='small'
                  sx={{ width: 150 }}
                  renderInput={(params) => <TextField {...params} label="Region" />}
              />
              <Autocomplete
                disablePortal
                id="combo-box-demo"
                options={[]}
                size='small'
                sx={{ width: 150, marginLeft: 2 }}
                renderInput={(params) => <TextField {...params} label="Dealer" />}
              />
            </Grid> 

            <Grid container sx={{ padding:1 }}>
                <DataTable
                    hideFooter
                    disableColumnMenu
                    checkboxSelection
                    tableHeight={890}
                    rowHeight={100}
                    rows={booking}
                    columns={columns}
                    // selectionModel={selectedSpecList}
                    // onSelectionModelChange={handleSelectSpecification}
                    getRowId={(params) => params.orderNo}
                />
            </Grid>

            <Copyright />
        </Box>
      </Box>
    </ThemeProvider>
  );
}