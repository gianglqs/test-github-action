import { useState } from 'react';

import { useDispatch, useSelector } from 'react-redux';
import { indicatorStore, commonStore, bookingStore } from '@/store/reducers';
import { Button } from '@mui/material';
import {
   AppLayout,
   DataTablePagination,
   AppDateField,
   DataTable,
   AppTextField,
   AppAutocomplete,
} from '@/components';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';

import LineChart from '@/components/chart/Line';

import {
   Chart as ChartJS,
   LinearScale,
   PointElement,
   Tooltip,
   LineElement,
   Legend,
   CategoryScale,
} from 'chart.js';
import { Bubble, Line } from 'react-chartjs-2';
import { faker } from '@faker-js/faker';

import { defaultValueFilterBooking } from '@/utils/defaultValues';
import { produce } from 'immer';
import _ from 'lodash';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend);

export default function Indicators() {
   // const listIndicator = useSelector(indicatorStore.selectIndicatorList);

   const tableState = useSelector(commonStore.selectTableState);

   const initDataFilter = useSelector(indicatorStore.selectInitDataFilter);

   const [dataFilter, setDataFilter] = useState(defaultValueFilterBooking);

   console.log(initDataFilter.models);

   const dispatch = useDispatch();

   const handleChangeDataFilter = (option, field) => {
      setDataFilter((prev) =>
         produce(prev, (draft) => {
            if (_.includes(['orderNo', 'MarginPercetage', 'AOPMarginPercetage'], field)) {
               draft[field] = option;
            } else {
               draft[field] = option.map(({ value }) => value);
            }
         })
      );
   };

   const handleChangePage = (pageNo: number) => {
      dispatch(commonStore.actions.setTableState({ pageNo }));
      dispatch(indicatorStore.sagaGetList());
   };

   const handleChangePerPage = (perPage: number) => {
      dispatch(commonStore.actions.setTableState({ perPage }));
      handleChangePage(1);
   };

   const handleFilterIndicator = () => {
      dispatch(indicatorStore.actions.setDefaultValueFilterIndicator(dataFilter));
      handleChangePage(1);
   };

   const formatNumber = (num: number) => {
      return num.toLocaleString(undefined, {
         minimumFractionDigits: 2,
         maximumFractionDigits: 2,
      });
   };

   const columns = [
      {
         field: 'orderNo',
         flex: 0.8,
         headerName: 'Region',
      },
      {
         field: 'region',
         flex: 0.8,
         headerName: 'Plant',
         renderCell(params) {
            return <span>{params.row.region.region}</span>;
         },
      },
      {
         field: 'ctryCode',
         flex: 0.8,
         headerName: 'Class',
      },
      {
         field: 'dealerName',
         flex: 1.2,
         headerName: 'Series',
      },
      {
         field: 'Plant',
         flex: 0.8,
         headerName: 'Average Dealer Net',
         renderCell(params) {
            return <span>{params.row.productDimension?.plant}</span>;
         },
      },
      {
         field: 'truckClass',
         flex: 0.8,
         headerName: '2022 Actual',
         renderCell(params) {
            return <span>{params.row.productDimension?.clazz}</span>;
         },
      },
      {
         field: 'series',
         flex: 0.8,
         headerName: '2023 AOPF',
         renderCell(params) {
            return <span>{params.row.series}</span>;
         },
      },
      {
         field: 'model',
         flex: 0.8,
         headerName: '2024 LRFF',
         renderCell(params) {
            return <span>{params.row.model}</span>;
         },
      },
      {
         field: 'quantity',
         flex: 0.8,
         headerName: 'HYG Lead Time',
      },
      {
         field: 'totalCost',
         flex: 0.8,
         headerName: 'Competitor Lead Time',
         renderCell(params) {
            return <span>{formatNumber(params?.row.totalCost)}</span>;
         },
      },
      {
         field: 'dealerNet',
         flex: 0.8,
         headerName: 'Dealer Street Pricing(USD)',
         renderCell(params) {
            return <span>{formatNumber(params?.row.dealerNet)}</span>;
         },
      },
      {
         field: 'dealerNetAfterSurCharge',
         flex: 0.8,
         headerName: 'Dealer Handling Cost',
         renderCell(params) {
            return <span>{formatNumber(params?.row.dealerNetAfterSurCharge)}</span>;
         },
      },
      {
         field: 'marginAfterSurCharge',
         flex: 1,
         headerName: 'Dealer Pricing Premium/Margin (USD)',
         renderCell(params) {
            return <span>{formatNumber(params?.row.marginAfterSurCharge)}</span>;
         },
      },

      {
         field: 'marginPercentageAfterSurCharge',
         flex: 1,
         headerName: 'Dealer Premium / Margin %',
         renderCell(params) {
            return <span>{formatNumber(params?.row.marginPercentageAfterSurCharge * 100)}%</span>;
         },
      },
      {
         field: 'aopmarginPercentage',
         flex: 1,
         headerName: 'Competition Pricing (USD)',
         renderCell(params) {
            return <span>{formatNumber(params?.row.aopmarginPercentage * 100)}%</span>;
         },
      },
      {
         field: 'th',
         flex: 1,
         headerName: 'Competitor Name',
         renderCell(params) {
            return <span>{formatNumber(params?.row.aopmarginPercentage * 100)}%</span>;
         },
      },
      {
         field: 'aopmarginPercthtentage',
         flex: 1,
         headerName: 'Varian % (Competitor - (Dealer Street + Premium))',
         renderCell(params) {
            return <span>{formatNumber(params?.row.aopmarginPercentage * 100)}%</span>;
         },
      },
   ];

   const options = {
      scales: {
         y: {
            beginAtZero: true,
         },
      },
   };
   const data = {
      datasets: [
         {
            label: 'Red dataset',
            data: Array.from({ length: 10 }, () => ({
               x: faker.number.int({ min: -100, max: 100 }),
               y: faker.number.int({ min: -100, max: 100 }),
               r: faker.number.int({ min: 5, max: 20 }),
            })),
            backgroundColor: 'rgba(88, 24, 69, 0.5)',
         },
         {
            label: 'Blue dataset',
            data: Array.from({ length: 10 }, () => ({
               x: faker.number.int({ min: -100, max: 100 }),
               y: faker.number.int({ min: -100, max: 100 }),
               r: faker.number.int({ min: 5, max: 20 }),
            })),
            backgroundColor: 'rgba(53, 162, 235, 0.5)',
         },
      ],
   };

   const optionsLineChart = {
      responsive: true,
      plugins: {
         legend: {
            position: 'top' as const,
         },
         title: {
            display: true,
            text: 'Son Giang',
         },
      },
   };

   const labels = [2022, 2023, 2024];

   const currentDate = new Date();

   let year = currentDate.getFullYear();

   //create array Year use to Label, vd: 2022,2023,2024,2025,...
   const arrayYear = Array.from({ length: 5 }, (_, i) => i + year - 1);

   console.log(arrayYear);

   const dataLineChart = {
      labels,
      datasets: [
         {
            label: 'ASIA',
            data: labels.map(() => faker.datatype.number({ min: -1000, max: 1000 })),
            borderColor: 'rgb(88, 24, 69)',
            backgroundColor: 'rgba(88, 24, 69, 0.7)',
         },
         {
            label: 'India',
            data: [200, 400, 600, 800, 1000],
            borderColor: 'rgb(53, 162, 235)',
            backgroundColor: 'rgba(53, 162, 235, 0.5)',
         },
         {
            label: 'China',
            data: labels.map(() => faker.datatype.number({ min: -1000, max: 1000 })),
            borderColor: '#17a9a3',
            backgroundColor: '#17a9a3',
         },
      ],
   };

   const dataForLineChartRegion = {
      arrayYear,
      datasets: [],
   };

   // create scrollbar for table

   return (
      <>
         <AppLayout entity="indicator" heightBody={1100}>
            <Grid container spacing={1}>
               <Grid item xs={2} sx={{ zIndex: 10, height: 25 }}>
                  <AppAutocomplete
                     options={initDataFilter.regions}
                     label="Region"
                     onChange={(e, option) => handleChangeDataFilter(option, 'regions')}
                     limitTags={2}
                     disableListWrap
                     primaryKeyOption="value"
                     multiple
                     disableCloseOnSelect
                     renderOption={(prop, option) => `${option.value}`}
                     getOptionLabel={(option) => `${option.value}`}
                  />
               </Grid>
               <Grid item xs={2} sx={{ zIndex: 10, height: 25 }}>
                  <AppAutocomplete
                     options={initDataFilter.dealers}
                     label="Dealer"
                     sx={{ height: 25, zIndex: 10 }}
                     onChange={(e, option) => handleChangeDataFilter(option, 'dealers')}
                     limitTags={1}
                     disableListWrap
                     primaryKeyOption="value"
                     multiple
                     disableCloseOnSelect
                     renderOption={(prop, option) => `${option.value}`}
                     getOptionLabel={(option) => `${option.value}`}
                  />
               </Grid>
               <Grid item xs={2} sx={{ zIndex: 10, height: 25 }}>
                  <AppAutocomplete
                     options={initDataFilter.plants}
                     label="Plant"
                     sx={{ height: 25, zIndex: 10 }}
                     onChange={(e, option) => handleChangeDataFilter(option, 'plants')}
                     limitTags={1}
                     disableListWrap
                     primaryKeyOption="value"
                     multiple
                     disableCloseOnSelect
                     renderOption={(prop, option) => `${option.value}`}
                     getOptionLabel={(option) => `${option.value}`}
                  />
               </Grid>
               <Grid item xs={2}>
                  <AppAutocomplete
                     options={initDataFilter.metaSeries}
                     label="MetaSeries"
                     sx={{ height: 25, zIndex: 10 }}
                     onChange={(e, option) => handleChangeDataFilter(option, 'metaSeries')}
                     limitTags={1}
                     disableListWrap
                     primaryKeyOption="value"
                     multiple
                     disableCloseOnSelect
                     renderOption={(prop, option) => `${option.value}`}
                     getOptionLabel={(option) => `${option.value}`}
                  />
               </Grid>

               <Grid item xs={2}>
                  <AppAutocomplete
                     options={initDataFilter.classes}
                     label="Class"
                     sx={{ height: 25, zIndex: 10 }}
                     onChange={(e, option) => handleChangeDataFilter(option, 'classes')}
                     limitTags={1}
                     disableListWrap
                     primaryKeyOption="value"
                     multiple
                     disableCloseOnSelect
                     renderOption={(prop, option) => `${option.value}`}
                     getOptionLabel={(option) => `${option.value}`}
                  />
               </Grid>
               <Grid item xs={2}>
                  <AppAutocomplete
                     options={initDataFilter.models}
                     label="Model"
                     sx={{ height: 25, zIndex: 10 }}
                     onChange={(e, option) => handleChangeDataFilter(option, 'models')}
                     limitTags={1}
                     disableListWrap
                     primaryKeyOption="value"
                     multiple
                     disableCloseOnSelect
                     renderOption={(prop, option) => `${option.value}`}
                     getOptionLabel={(option) => `${option.value}`}
                  />
               </Grid>

               <Grid item xs={4}>
                  <Grid item xs={6} sx={{ paddingRight: 0.5 }}>
                     <AppAutocomplete
                        options={initDataFilter.chineseBrands}
                        label="Chinese Brand"
                        onChange={(e, option) =>
                           handleChangeDataFilter(
                              _.isNil(option) ? '' : option?.value,
                              'MarginPercetage'
                           )
                        }
                        disableClearable={false}
                        primaryKeyOption="value"
                        renderOption={(prop, option) => `${option.value}`}
                        getOptionLabel={(option) => `${option.value}`}
                     />
                  </Grid>
               </Grid>

               <Grid item xs={2}>
                  <AppAutocomplete
                     options={initDataFilter.marginPercentageGrouping}
                     label="AOP Margin % Group"
                     primaryKeyOption="value"
                     onChange={(e, option) =>
                        handleChangeDataFilter(
                           _.isNil(option) ? '' : option?.value,
                           'AOPMarginPercetage'
                        )
                     }
                     disableClearable={false}
                     renderOption={(prop, option) => `${option.value}`}
                     getOptionLabel={(option) => `${option.value}`}
                  />
               </Grid>

               <Grid item xs={2}>
                  <Button
                     variant="contained"
                     onClick={handleFilterIndicator}
                     sx={{ width: '100%', height: 24 }}
                  >
                     Filter
                  </Button>
               </Grid>
            </Grid>
            <Paper elevation={1} sx={{ marginTop: 2 }}>
               <Grid container>
                  <DataTable
                     hideFooter
                     disableColumnMenu
                     tableHeight={610}
                     rowHeight={45}
                     rows={[]}
                     rowBuffer={35}
                     rowThreshold={25}
                     columns={columns}
                     getRowId={(params) => params.orderNo}
                  />
               </Grid>
               <DataTablePagination
                  page={tableState.pageNo}
                  perPage={tableState.perPage}
                  totalItems={tableState.totalItems}
                  onChangePage={handleChangePage}
                  onChangePerPage={handleChangePerPage}
               />
            </Paper>

            <Grid
               container
               spacing={1}
               justifyContent="center"
               alignItems="center"
               sx={{ margin: '20px 0' }}
            >
               <Grid item xs={4}>
                  <Bubble options={options} data={data} />
               </Grid>

               <Grid item xs={4}>
                  <LineChart chartData={dataLineChart} />
               </Grid>

               <Grid item xs={4}>
                  <Line options={optionsLineChart} data={dataLineChart} />
               </Grid>
            </Grid>
         </AppLayout>
      </>
   );
}
