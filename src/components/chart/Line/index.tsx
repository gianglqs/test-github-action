import React from 'react';
import { Line } from 'react-chartjs-2';

function LineChart({ chartName, chartData }) {
   return (
      <div className="chart-container">
         <Line
            data={chartData}
            options={{
               plugins: {
                  title: {
                     display: true,
                     text: chartName,
                  },
                  legend: {
                     display: true,
                  },
                  tooltip: {
                     enabled: true,
                  },
               },
            }}
         />
      </div>
   );
}
export default LineChart;
