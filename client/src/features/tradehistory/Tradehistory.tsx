import React from 'react';
import Table from 'react-bootstrap/Table';
import { Trade } from '../../types/types';

import '../tableView.css';

interface TradeHistoryProps {
    tradeHistory: Array<Trade>;
    id: string;
}

export default function TradeHistory({ tradeHistory, id }: TradeHistoryProps) {
    const headers = (
        <tr>
            <th>size</th>
            <th>price</th>
            <th>time</th>
        </tr>
    );

    const rows = tradeHistory.map((trade, i) => {
        return (
            <tr key={i}>
                <td>{trade.quantity.toFixed(2)}</td>
                <td>{'£' + trade.price.toFixed(2)}</td>
                <td>{new Date(trade.datetime).toTimeString().split(' ')[0]}</td>
            </tr>
        );
    });

    return (
        <div className="tableView" id={id}>
            <div className="headerContainer">
                <h4 className="title">Trade history</h4>
            </div>
            <div className="tableContainer">
                <Table borderless hover size="sm" className="no-margin">
                    <thead>{headers}</thead>
                    <tbody data-cy="tradeHistoryTableBody">{rows}</tbody>
                </Table>
            </div>
        </div>
    );
}
