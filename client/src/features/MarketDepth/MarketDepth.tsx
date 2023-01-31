import React from 'react';
import { OrderbookItem } from '../../types/types';
import { DepthChart } from './DepthChart/DepthChart';
import '../tableView.css';

type MarketDepthProps = {
    id: string;
    buyDepthData: OrderbookItem[];
    sellDepthData: OrderbookItem[];
};

export default function MarketDepth({
    id,
    buyDepthData,
    sellDepthData,
}: MarketDepthProps) {
    return (
        <div id={id} className="tableView">
            <div className="headerContainer">
                <h4 className="title">Market depth</h4>
            </div>
            <DepthChart
                width={400}
                height={400}
                buyDepthData={buyDepthData}
                sellDepthData={sellDepthData}
            />
        </div>
    );
}
