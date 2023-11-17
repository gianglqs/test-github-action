import { useState } from 'react';

import { useDispatch, useSelector } from 'react-redux';
import { indicatorStore, commonStore } from '@/store/reducers';
import { Button } from '@mui/material';
import { AppLayout, DataTablePagination, DataTable, AppAutocomplete } from '@/components';
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
import { Bubble } from 'react-chartjs-2';
import { faker } from '@faker-js/faker';

import { defaultValueFilterIndicator } from '@/utils/defaultValues';
import { produce } from 'immer';
import _ from 'lodash';
import indicatorApi from '@/api/indicators.api';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Legend);

export default function Indicators() {
   const dispatch = useDispatch();

   const tableState = useSelector(commonStore.selectTableState);

   // select data Filter in store
   const initDataFilter = useSelector(indicatorStore.selectInitDataFilter);

   const [dataFilter, setDataFilter] = useState(defaultValueFilterIndicator);

   const getDataForTable = useSelector(indicatorStore.selectIndicatorList);

   // Select data line Chart Region in store
   const dataForLineChartRegion = useSelector(indicatorStore.selectDataForLineChartRegion);
   const dataForLineChartPlant = useSelector(indicatorStore.selectDataForLineChartPLant);

   const [competitiveLandscapeData, setCompetitiveLandscapeData] = useState({
      datasets: [
         {
            label: 'Red',
            data: Array.from({ length: 5 }, () => ({
               x: faker.number.int({ min: -100, max: 100 }),
               y: faker.number.int({ min: -100, max: 100 }),
               r: faker.number.int({ min: 5, max: 20 }),
            })),
            backgroundColor: 'rgba(88, 24, 69, 0.5)',
         },
         {
            label: 'Blue',
            data: Array.from({ length: 5 }, () => ({
               x: faker.number.int({ min: -100, max: 100 }),
               y: faker.number.int({ min: -100, max: 100 }),
               r: faker.number.int({ min: 5, max: 20 }),
            })),
            backgroundColor: 'rgba(53, 162, 235, 0.5)',
         },
      ],
   });

   // I think you can write a common function here
   const [countryValue, setCountryValue] = useState();
   const [competitorClassValue, setCompetitorClass] = useState();
   const [categoryValue, setCategory] = useState();
   const [seriesValue, setSeries] = useState();

   const handleChooseCountry = (value) => {
      setCountryValue(value.innerText);
   };
   const handleChooseClass = (value) => {
      setCompetitorClass(value.innerText);
   };
   const handleChooseCategory = (value) => {
      setCategory(value.innerText);
   };
   const handleChooseSeries = (value) => {
      setSeries(value.innerText);
   };

   const handleFilterCompetitiveLandscape = async () => {
      try {
         const {
            data: { competitiveLandscape },
         } = await indicatorApi.getCompetitiveLandscape({
            country: countryValue,
            clazz: competitorClassValue,
            category: categoryValue,
            series: seriesValue,
         });
         const randomNum = () => Math.floor(Math.random() * (235 - 52 + 1) + 52);
         const datasets = competitiveLandscape.map((item) => {
            return {
               label: item.competitorName,
               data: [
                  {
                     x: item.competitorLeadTime,
                     y: item.competitorPricing,
                     r: item.marketShare * 100,
                  },
               ],
               backgroundColor: `rgb(${randomNum()}, ${randomNum()}, ${randomNum()})`,
            };
         });

         setCompetitiveLandscapeData({
            datasets: datasets,
         });
      } catch (error) {
         dispatch(commonStore.actions.setErrorMessage(error.message));
      }
   };

   // const handleFilterCompetitiveLandscape = () => {
   //    let cookies = parseCookies();
   //    let token = cookies['token'];
   //    axios({
   //       method: 'post',
   //       url: `${process.env.NEXT_PUBLIC_BACKEND_URL}charts/competitiveLandscape`,
   //       data: {
   //          country: countryValue,
   //          clazz: competitorClassValue,
   //          category: categoryValue,
   //          series: seriesValue,
   //       },
   //       headers: {
   //          'Content-Type': 'application/json',
   //          Authorization: 'Bearer' + token,
   //       },
   //    })
   //       .then(function (response) {
   //          const datasets = [];

   //          const randomNum = () => Math.floor(Math.random() * (235 - 52 + 1) + 52);

   //          const data = response.data.competitiveLandscape;

   //          data.forEach((cp) => {
   //             const element = {
   //                label: cp.competitorName,
   //                data: [
   //                   {
   //                      x: cp.competitorLeadTime,
   //                      y: cp.competitorPricing,
   //                      r: cp.marketShare * 100,
   //                   },
   //                ],
   //                backgroundColor: `rgb(${randomNum()}, ${randomNum()}, ${randomNum()})`,
   //             };

   //             datasets.push(element);
   //          });

   //          setCompetitiveLandscapeData({
   //             datasets: datasets,
   //          });
   //       })
   //       .catch(function (response) {
   //          console.log(response);
   //       });
   // };

   const handleChangeDataFilter = (option, field) => {
      setDataFilter((prev) =>
         produce(prev, (draft) => {
            if (_.includes(['chineseBrand', 'aopMarginPercentageGroup'], field)) {
               draft[field] = option.value;
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

   const formatNumber = (num: any) => {
      if (typeof num === 'number') {
         return num.toLocaleString(undefined, {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
         });
      } else {
         return null;
      }
   };

   const columns = [
      {
         field: 'competitorName',
         flex: 1,
         headerName: 'Competitor Name',
      },

      {
         field: 'region',
         flex: 0.8,
         headerName: 'Region',
      },
      {
         field: 'plant',
         flex: 0.8,
         headerName: 'Plant',
      },
      {
         field: 'clazz',
         flex: 0.8,
         headerName: 'Class',
      },
      {
         field: 'series',
         flex: 1.2,
         headerName: 'Series',
      },
      {
         field: 'averageDN',
         flex: 0.8,
         headerName: 'Average Dealer Net',
         renderCell(params) {
            return <span>{formatNumber(params.row.averageDN)}</span>;
         },
      },
      {
         field: 'actual',
         flex: 0.8,
         headerName: '2022 Actual',
         renderCell(params) {
            return <span>{formatNumber(params.row.actual)}</span>;
         },
      },
      {
         field: 'aopf',
         flex: 0.8,
         headerName: '2023 AOPF',
         renderCell(params) {
            return <span>{formatNumber(params.row.aopf)}</span>;
         },
      },
      {
         field: 'lrff',
         flex: 0.8,
         headerName: '2024 LRFF',
         renderCell(params) {
            return <span>{formatNumber(params.row.lrff)}</span>;
         },
      },
      // {
      //    field: 'hygleadTime',
      //    flex: 0.8,
      //    headerName: 'HYG Lead Time',
      // },
      {
         field: 'competitorLeadTime',
         flex: 0.8,
         headerName: 'Competitor Lead Time',
      },
      {
         field: 'dealerStreetPricing',
         flex: 0.8,
         headerName: 'Dealer Street Pricing(USD)',
      },
      {
         field: 'dealerHandlingCost',
         flex: 0.8,
         headerName: 'Dealer Handling Cost',
         renderCell(params) {
            return <span>{formatNumber(params.row.dealerHandlingCost)}</span>;
         },
      },
      {
         field: 'competitorPricing',
         flex: 1,
         headerName: 'Competition Pricing (USD)',
         renderCell(params) {
            return <span>{formatNumber(params.row.competitorPricing)}</span>;
         },
      },
      {
         field: 'dealerPricingPremiumPercentage',
         flex: 1,
         headerName: 'Dealer Pricing Premium/Margin (USD)',
         renderCell(params) {
            return <span>{formatNumber(params.row.dealerPricingPremiumPercentage)}</span>;
         },
      },

      {
         field: 'dealerPremiumPercentage',
         flex: 1,
         headerName: 'Dealer Premium / Margin %',
         renderCell(params) {
            return <span>{formatNumber(params.row.dealerPremiumPercentage * 100)}%</span>;
         },
      },

      {
         field: 'variancePercentage',
         flex: 1,
         headerName: 'Varian % (Competitor - (Dealer Street + Premium))',
         renderCell(params) {
            return <span>{formatNumber(params.row.variancePercentage * 100)}%</span>;
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

   const labels = [2022, 2023, 2024];

   const currentDate = new Date();

   let year = currentDate.getFullYear();

   //create array Year use to Label, vd: 2022,2023,2024,2025,...
   const arrayYear = Array.from({ length: 3 }, (_, i) => i + year - 1);

   const arrayColor = ['#17a9a3', '#3f0e03', '#147384', '#0048bd', '#005821', '#ec9455', '#ffafa6'];

   const modifyDataLineChartRegion = {
      labels,
      datasets: dataForLineChartRegion.map((e, index) => ({
         label: e.region,
         data: [e.actual, e.aopf, e.lrff],
         borderColor: arrayColor[index],
         backgroundColor: arrayColor[index],
      })),
   };

   const modifyDataLineChartPlant = {
      labels,
      datasets: dataForLineChartPlant.map((e, index) => ({
         label: e.plant,
         data: [e.actual, e.aopf, e.lrff],
         borderColor: arrayColor[index],
         backgroundColor: arrayColor[index],
      })),
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

               <Grid item xs={2}>
                  <AppAutocomplete
                     options={initDataFilter.chineseBrands}
                     label="Chinese Brand"
                     onChange={
                        (e, option) =>
                           handleChangeDataFilter(_.isNil(option) ? '' : option, 'chineseBrand')
                        //   handleChangeDataFilter(option, 'chineseBrand')
                     }
                     disableClearable={false}
                     primaryKeyOption="value"
                     renderOption={(prop, option) => `${option.value}`}
                     getOptionLabel={(option) => `${option.value}`}
                  />
               </Grid>

               <Grid item xs={2}>
                  <AppAutocomplete
                     options={initDataFilter.marginPercentageGrouping}
                     label="AOP Margin % Group"
                     primaryKeyOption="value"
                     onChange={
                        (e, option) =>
                           handleChangeDataFilter(
                              _.isNil(option) ? '' : option,
                              'aopMarginPercentageGroup'
                           )
                        // handleChangeDataFilter(option, 'aopMarginPercentageGroup')
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
                     rows={getDataForTable}
                     rowBuffer={35}
                     rowThreshold={25}
                     columns={columns}
                     getRowId={(params) => params.id}
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
               <Grid container spacing={1}>
                  <Grid item xs={1} sx={{ zIndex: 10, height: 25 }}>
                     <AppAutocomplete
                        options={initDataFilter.countries}
                        label="Country"
                        onChange={(e, option) => handleChooseCountry(e.target)}
                        limitTags={2}
                        disableListWrap
                        primaryKeyOption="value"
                        disableCloseOnSelect
                        renderOption={(prop, option) => `${option.value}`}
                        getOptionLabel={(option) => `${option.value}`}
                     />
                  </Grid>
                  <Grid item xs={1} sx={{ zIndex: 10, height: 25 }}>
                     <AppAutocomplete
                        options={initDataFilter.classes}
                        label="Competitor Class"
                        onChange={(e, option) => handleChooseClass(e.target)}
                        limitTags={2}
                        disableListWrap
                        primaryKeyOption="value"
                        disableCloseOnSelect
                        renderOption={(prop, option) => `${option.value}`}
                        getOptionLabel={(option) => `${option.value}`}
                     />
                  </Grid>
                  <Grid item xs={1} sx={{ zIndex: 10, height: 25 }}>
                     <AppAutocomplete
                        options={initDataFilter.categories}
                        label="Category"
                        onChange={(e, option) => handleChooseCategory(e.target)}
                        limitTags={2}
                        disableListWrap
                        primaryKeyOption="value"
                        disableCloseOnSelect
                        renderOption={(prop, option) => `${option.value}`}
                        getOptionLabel={(option) => `${option.value}`}
                     />
                  </Grid>
                  <Grid item xs={1} sx={{ zIndex: 10, height: 25 }}>
                     <AppAutocomplete
                        options={initDataFilter.series}
                        label="Series"
                        onChange={(e, option) => handleChooseSeries(e.target)}
                        limitTags={2}
                        disableListWrap
                        primaryKeyOption="value"
                        disableCloseOnSelect
                        renderOption={(prop, option) => `${option.value}`}
                        getOptionLabel={(option) => `${option.value}`}
                     />
                  </Grid>

                  <Grid item xs={1}>
                     <Button
                        variant="contained"
                        onClick={handleFilterCompetitiveLandscape}
                        sx={{ width: '25%', height: 24 }}
                     >
                        Filter
                     </Button>
                  </Grid>
               </Grid>
               <Grid item xs={4}>
                  <Bubble options={options} data={competitiveLandscapeData} />
               </Grid>

               <Grid item xs={4}>
                  <LineChart
                     chartData={modifyDataLineChartRegion}
                     chartName={'Forecast Volume by Year & Region'}
                  />
               </Grid>

               <Grid item xs={4}>
                  <LineChart
                     chartData={modifyDataLineChartPlant}
                     chartName={'Forecast Volume by Year & Plant'}
                  />
               </Grid>
            </Grid>
         </AppLayout>
      </>
   );
}
