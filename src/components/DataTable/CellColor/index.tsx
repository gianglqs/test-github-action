import { formatNumber, formatNumberPercentage } from '@/utils/formatCell';

export default function CellColor({ color, value }) {
   return (
      <p
         style={{
            backgroundColor: color,
            // width: '100%',
            height: '90%',
            textAlign: 'right',
            display: 'flex',
            alignItems: 'center',
            padding: '0 10px',
         }}
      >
         {formatNumber(value)}
      </p>
   );
}

export function CellBEPColor({ color, value }) {
   return (
      <p
         style={{
            backgroundColor: color,
            width: '100%',
            height: '90%',
            textAlign: 'right',
            display: 'flex',
            alignItems: 'center',
            padding: '0 10px',
         }}
      >
         {value}
      </p>
   );
}

export function CellPercentageColor({ color, value }) {
   return (
      <p
         style={{
            backgroundColor: color,
            // width: '100%',
            height: '90%',
            textAlign: 'right',
            display: 'flex',
            alignItems: 'center',
            padding: '0 10px',
         }}
      >
         {formatNumberPercentage(value)}
      </p>
   );
}
