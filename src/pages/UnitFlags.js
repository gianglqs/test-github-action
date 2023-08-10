import { DataGrid } from "@mui/x-data-grid";
import React from "react";
import axios from "axios";
import Cookies from "universal-cookie";

class UnitFlags extends React.Component{

    constructor() {
        super();
        this.state = {
                data: [],
                error: null,
            };
    }
    cookie = new Cookies();
    BASE_URL = "http://localhost:8080";
    access_token = this.cookie.get("access_token");
    headers = { headers: {"Authorization": `Bearer${this.access_token}`} };

    componentDidMount() {
        axios.get(this.BASE_URL + "/unitFlags", this.headers)
        .then(response => {
            this.setState({data: response.data});
            this.setState({error: null});
        })
        .catch(error => {
            this.setState({error: error});
        });
    }

    render() {
        var rows = Array.from(this.state.data);
        var width = 200;
        var columns = [
            { field: 'unit',                    headerName: 'Unit',                     width: width },
            { field: 'description',             headerName: 'Description',              width: width },
            { field: 'uclass',                  headerName: 'Class',                    width: width},
            { field: 'readyForDistribution',    headerName: 'Ready for Distribution',   width: width},
            { field: 'enableGLReadiness',       headerName: 'Enable GL Readiness',      width: width},
            { field: 'fullyAttributed',         headerName: 'Fully Attributed',         width: width},
            { field: 'readyForPartsCosting',    headerName: 'Ready for Parts Costing',  width: width},
            { field: 'createdDate',             headerName: 'Created Date',             width: width},
            { field: 'cancelled',               headerName: 'Cancelled',                width: width},
        ];
        console.log(this.state.data);
        return (
            <div style={{ height: '800px', width: '100%' }}>
                <DataGrid rows={rows} columns={columns} 
                    getRowId={(row) => row.unit} 
                    sx={{
                        border: 5,
                        borderColor: 'black',
                        backgroundColor: 'aliceblue'
                    }}/>
            </div>
        );
    }
}
export default UnitFlags;
