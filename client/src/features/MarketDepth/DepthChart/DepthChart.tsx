import React, { useEffect, useRef, useState } from 'react';

import * as d3 from 'd3';
import { AxisLeft } from './AxisLeft';
import { AxisBottom } from './AxisBottom';
import { OrderbookItem } from '../../../types/types';
import './depthChart.css';

const marginSize = 30;
const MARGIN = {
    top: marginSize,
    right: marginSize,
    bottom: marginSize * 1.5,
    left: marginSize * 1.5,
};

type DepthChartProps = {
    width: number;
    height: number;
    buyDepthData: OrderbookItem[];
    sellDepthData: OrderbookItem[];
};

function makeSteps(depthPoints: OrderbookItem[]) {
    for (let i = depthPoints.length - 2; i >= 0; i--) {
        const p1 = depthPoints[i];
        const p2 = depthPoints[i + 1];
        const upper = p1.quantity >= p2.quantity ? p1 : p2;
        const lower = p1.quantity < p2.quantity ? p1 : p2;
        const newP = { price: upper.price, quantity: lower.quantity };

        // insert new point
        depthPoints.splice(i + 1, 0, newP);
    }
}

export const DepthChart = ({
    buyDepthData,
    sellDepthData,
}: DepthChartProps) => {
    const svgContainer = useRef<HTMLDivElement>(null); // The PARENT of the SVG

    // State to track width and height of SVG Container
    const [width, setWidth] = useState(400);
    const [height, setHeight] = useState(400);

    // This function calculates width and height of the container
    const getSvgContainerSize = () => {
        if (svgContainer?.current === null) return;
        const newWidth = svgContainer.current.clientWidth;
        setWidth(newWidth);

        const newHeight = svgContainer.current.clientHeight;
        setHeight(newHeight);
    };

    useEffect(() => {
        // detect 'width' and 'height' on render
        getSvgContainerSize();
        // listen for resize changes, and detect dimensions again when they change
        window.addEventListener('resize', getSvgContainerSize);
        // cleanup event listener
        return () => window.removeEventListener('resize', getSvgContainerSize);
    }, []);

    // get limits of data
    const minSellPrice = sellDepthData.reduce(
        (min, d) => Math.min(min, d.price),
        Infinity
    );
    const maxSellPrice = sellDepthData.reduce(
        (max, d) => Math.max(max, d.price),
        -Infinity
    );
    const maxSellQuantity = sellDepthData.reduce(
        (max, d) => Math.max(max, d.quantity),
        -Infinity
    );
    const minBuyPrice = buyDepthData.reduce(
        (min, d) => Math.min(min, d.price),
        Infinity
    );
    const maxBuyPrice = buyDepthData.reduce(
        (max, d) => Math.max(max, d.price),
        -Infinity
    );
    const maxBuyQuantity = buyDepthData.reduce(
        (max, d) => Math.max(max, d.quantity),
        -Infinity
    );

    // define limits of graph
    const extraPriceMargin = Math.max((maxSellPrice - minBuyPrice) * 0.03, 0.5);
    const xMin = Math.min(minBuyPrice, minSellPrice) - extraPriceMargin;
    const xMax = Math.max(maxBuyPrice, maxSellPrice) + extraPriceMargin;
    const yMin = 0;
    const yMax = Math.max(maxBuyQuantity, maxSellQuantity);

    // Layout. The div size is set by the given props.
    // The bounds (=area inside the axis) is calculated by substracting the margins
    const boundsWidth = width - MARGIN.right - MARGIN.left;
    const boundsHeight = height - MARGIN.top - MARGIN.bottom;

    // Scales
    const yScale = d3
        .scaleLinear()
        .domain([yMin, yMax])
        .range([boundsHeight, 0]);
    const xScale = d3
        .scaleLinear()
        .domain([xMin, xMax])
        .range([0, boundsWidth]);

    const lineRenderer = d3.line();

    // make buy and sell lines
    const buyLine = () => {
        if (buyDepthData.length === 0) return <></>;
        const buyDepthPoints = [...buyDepthData];
        buyDepthPoints.sort((a, b) => a.price - b.price);
        makeSteps(buyDepthPoints);

        // close off line areas
        buyDepthPoints.push({ price: maxBuyPrice, quantity: 0 });
        buyDepthPoints.unshift({ price: xMin, quantity: maxBuyQuantity });
        buyDepthPoints.unshift({ price: xMin, quantity: 0 });

        const buyPath = lineRenderer(
            buyDepthPoints.map((dp) => [xScale(dp.price), yScale(dp.quantity)])
        );

        // need to convert type (string | null) to (string | undefined)
        const buyPathData = buyPath === null ? undefined : buyPath;

        return (
            <path
                d={buyPathData}
                stroke="darkGreen"
                fill="green"
                fillOpacity={hover === 2 ? 0.3 : 0.1}
                onMouseOver={() => setHover(2)}
                onMouseLeave={() => setHover(0)}
            ></path>
        );
    };

    const sellLine = () => {
        if (sellDepthData.length === 0) return <></>;
        const sellDepthPoints = [...sellDepthData];
        sellDepthPoints.sort((a, b) => a.price - b.price);
        makeSteps(sellDepthPoints);

        // close off line areas
        sellDepthPoints.push({
            price: xMax,
            quantity: maxSellQuantity,
        });
        sellDepthPoints.push({
            price: xMax,
            quantity: 0,
        });
        sellDepthPoints.unshift({ price: minSellPrice, quantity: 0 });

        // build path instructions
        const sellPath = lineRenderer(
            sellDepthPoints.map((dp) => [xScale(dp.price), yScale(dp.quantity)])
        );

        // need to convert type (string | null) to (string | undefined)
        const sellPathData = sellPath === null ? undefined : sellPath;

        return (
            <path
                d={sellPathData}
                stroke="darkRed"
                fill="red"
                fillOpacity={hover === 1 ? 0.3 : 0.1}
                onMouseOver={() => setHover(1)}
                onMouseLeave={() => setHover(0)}
            ></path>
        );
    };

    const [hover, setHover] = useState<number>(0);
    const [mousePos, setMousePos] = useState([-1, -1]);

    const handleMouseMove = (
        e: React.MouseEvent<SVGSVGElement, MouseEvent>
    ) => {
        const element = d3.select('svg').node() as HTMLElement;
        if (element === null) return;
        const x = e.clientX - element.getBoundingClientRect().x;
        const y = e.clientY - element.getBoundingClientRect().y;
        setMousePos([x, y]);
    };

    const handleMouseLeave = () => {
        setMousePos([-1, -1]);
    };

    // Handle Tooltip
    let tooltipVisible = true;

    if (mousePos.includes(-1)) tooltipVisible = false;

    const svgX = mousePos[0] - MARGIN.left;
    const graphX = xScale.invert(svgX);
    let tooltipY = 0;
    let graphY = 0;

    if (xMin < graphX && graphX < maxBuyPrice) {
        // over buy chart area

        const arr = [...buyDepthData];
        arr.sort((a, b) => a.price - b.price);

        let index = d3.bisect(
            arr.map((a) => a.price),
            graphX
        );
        if (index === arr.length) index--;
        graphY = arr[index].quantity;
        tooltipY = yScale(graphY) + MARGIN.top;
    } else if (minSellPrice < graphX && graphX < xMax) {
        // over sell chart area

        const arr = [...sellDepthData];
        arr.sort((a, b) => a.price - b.price);

        let index = d3.bisectLeft(
            arr.map((a) => a.price),
            graphX
        );
        index--; // honestly I don't why this is necessary, but it fixes the weird y offset problem I was having
        if (index > arr.length) index = arr.length - 1;

        graphY = arr[index].quantity;
        tooltipY = yScale(graphY) + MARGIN.top;
    } else if (maxBuyPrice < graphX && graphX < minSellPrice) {
        // mouse between buy and sell lines
        tooltipY = yScale(0) + MARGIN.top;
    } else tooltipVisible = false;

    const tooltipX = mousePos[0];

    const tooltip = () => {
        if (!tooltipVisible) return <></>;
        else
            return (
                <>
                    {/* x line */}
                    <line
                        x1={tooltipX}
                        y1={MARGIN.top}
                        x2={tooltipX}
                        y2={yScale(0) + MARGIN.top}
                        stroke="grey"
                        strokeWidth={1}
                        strokeDasharray="3 5"
                        pointerEvents="none"
                    ></line>
                    {/* y line */}
                    <line
                        x1={MARGIN.left}
                        y1={tooltipY}
                        x2={xScale(xMax) + MARGIN.left}
                        y2={tooltipY}
                        stroke="grey"
                        strokeWidth={1}
                        strokeDasharray="3 5"
                        pointerEvents="none"
                    ></line>
                    <g
                        transform={
                            'translate(' + tooltipX + ', ' + tooltipY + ')'
                        }
                    >
                        <circle cx={0} cy={0} r="3"></circle>
                    </g>
                </>
            );
    };

    const tooltipInfoBox = () => {
        const txtPrice = 'Price: £' + graphX.toFixed(2);
        const txtQuantity = 'Quantity: ' + graphY.toFixed(2);
        if (!tooltipVisible) return <></>;
        else
            return (
                <g transform={'translate(' + (MARGIN.left + 5) + ', 10)'}>
                    <rect
                        width="7em"
                        height="2em"
                        fill="#CCCCCC"
                        opacity="80%"
                        rx="5"
                    ></rect>
                    <text
                        style={{
                            fontSize: '10px',
                            // textAnchor: 'middle',
                            fill: '#000000',
                        }}
                        pointerEvents="none"
                    >
                        <tspan x="0" dy="1.2em">
                            {txtPrice}
                        </tspan>
                        <tspan x="0" dy="1.2em">
                            {txtQuantity}
                        </tspan>
                    </text>
                </g>
            );
    };

    return (
        <div className="svgContainer" ref={svgContainer}>
            <svg
                width={width}
                height={height}
                onMouseMove={handleMouseMove}
                onMouseLeave={handleMouseLeave}
            >
                <g
                    width={boundsWidth}
                    height={boundsHeight}
                    transform={`translate(${[MARGIN.left, MARGIN.top].join(
                        ','
                    )})`}
                >
                    {/* Y axis */}
                    <AxisLeft
                        yScale={yScale}
                        pixelsPerTick={40}
                        width={boundsWidth}
                    />

                    {/* X axis, use an additional translation to appear at the bottom */}
                    <g transform={`translate(0, ${boundsHeight})`}>
                        <AxisBottom
                            xScale={xScale}
                            pixelsPerTick={40}
                            height={boundsHeight}
                        />
                    </g>

                    {sellLine()}
                    {buyLine()}
                </g>

                {/* Y axis title */}
                <g transform={`translate(10, ${height / 2})`}>
                    <text
                        style={{
                            fontSize: '10px',
                            textAnchor: 'middle',
                            transform: 'rotate(-90deg)',
                            fill: '#A2A7A3',
                        }}
                    >
                        quantity
                    </text>
                </g>

                {/* X axis title */}
                <text
                    style={{
                        fontSize: '10px',
                        textAnchor: 'middle',
                        transform: `translate(${width / 2}px, ${
                            height - 10
                        }px)`,
                        fill: '#A2A7A3',
                    }}
                >
                    price
                </text>

                {tooltip()}
                {tooltipInfoBox()}
            </svg>
        </div>
    );
};
