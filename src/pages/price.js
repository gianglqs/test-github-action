import React from "react";
import { DataGrid } from "@mui/x-data-grid";
import axios from "axios";
import Cookies from "universal-cookie";

class PriceBook extends React.Component {

    constructor(props) {
        super(props);
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
        axios.get(this.BASE_URL + "/price", this.headers)
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
        var width = 150;
        var columns = [
            { field: 'id',              headerName: 'Id',               width: width},
            { field: 'updateAction',    headerName: 'Update Action',    width: width },
            { field: 'partNumber',      headerName: 'Part Number',      width: width },
            { field: 'customerType',    headerName: 'Customer Type',    width: width},
            { field: 'brand',           headerName: 'Brand',            width: width},
            { field: 'series',          headerName: 'Series',           width: width},
            { field: 'modelTruck',      headerName: 'Model Truck',      width: width},
            { field: 'currency',        headerName: 'Currency',         width: width},
            { field: 'price',           headerName: 'Price',            width: width},
            { field: 'soldAlonePrice',  headerName: 'Sold Alone Price', width: width},
            { field: 'startDate',       headerName: 'Start Date',       width: width},
            { field: 'endDate',         headerName: 'End Date',         width: width},
            { field: 'standard',        headerName: 'Standard',         width: width},
        ];
        return (
            <div style={{ height: '800px', width: '100%'}}>
                <DataGrid rows={rows} columns={columns} 
                    getRowId={(row) => row.id} 
                    sx={{
                        border: 5,
                        borderColor: 'black',
                        backgroundColor: 'aliceblue',
                    }}/>
            </div>
        )
    }

}
export default PriceBook;