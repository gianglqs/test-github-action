import { formatNumber, formatNumberPercentage } from '@/utils/formatCell';

export default function CellColor({ color, value }) {
   return (
      <p
         style={{
            backgroundColor: color,
            width: '100%',
            height: '100%',
            textAlign: 'right',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'flex-end',
            padding: '0 10px',
            borderRight: 'solid 1px white',
            zIndex: 0,
         }}
      >
         {formatNumber(value)}
      </p>
   );
}

export function CellText({ value }) {
   return (
      <p
         style={{
            width: '100%',
            height: '100%',
            display: 'flex',
            alignItems: 'center',
            padding: '0 10px',
            borderRight: 'solid 1px white',
         }}
      >
         {value}
      </p>
   );
}

export function NoneAdjustValueCell({ color, value }) {
   return (
      <p
         style={{
            backgroundColor: color,
            width: '100%',
            height: '100%',
            textAlign: 'right',
            justifyContent: 'flex-end',
            display: 'flex',
            alignItems: 'center',
            borderRight: 'solid 1px white',
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
            width: '100%',
            height: '100%',
            justifyContent: 'flex-end',
            textAlign: 'right',
            display: 'flex',
            alignItems: 'center',
            borderRight: 'solid 1px white',
            padding: '0 10px',
         }}
      >
         {formatNumberPercentage(value)}
      </p>
   );
}
