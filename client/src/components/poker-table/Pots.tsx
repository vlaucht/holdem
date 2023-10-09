
import React from "react";

interface PotsProps {
    pots: number[];
}

export const Pots: React.FC<PotsProps> = ({ pots }) => {
    return (
        <div className="pots" >
            {pots.map((pot, index) => (
                <div key={index} className="pot" style={{fontSize: '1.1em'}}>
                    <label>{index === 0 ? 'Main Pot: ' : 'Side Pot ' + index + ': '}</label>
                    ${pot}
                </div>
            ))}
        </div>
    );
};