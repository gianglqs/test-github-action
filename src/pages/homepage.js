import React from "react";
import '../resources/homePage.css';
import PriceBook from "./price";
import UnitFlags from "./UnitFlags";

class HomePage extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
                currentTab: "Unit Flags"
            };
    }

    onPointed(id) {
        this.resetPointed();
        var category = document.getElementById(id);
        category.className = "category-pointed";

        this.setState({currentTab: category.innerText});
    }
    resetPointed() {
        var categories = Array.from(document.getElementsByClassName("category-pointed"));
        if(categories.length > 0) {
            categories.forEach(cat => {
                cat.className = "category";
            });
        }
    }

    render() {
        // if on whether UnitFlags / Price --> show its table of content
        var contentTable;
        if(this.state.currentTab === "Unit Flags") {
            contentTable = <UnitFlags />;
        }
        else {
            contentTable = <PriceBook />
        }

        return (
            <div className="container">
                <div className="logo">
                    <img src="https://s21.q4cdn.com/775754248/files/images/logo.svg" alt="hyster-yale-logo"></img>
                </div>
    
                <div className="contentTable">
                        {contentTable}
                </div>

                <div className="sideBar">
                    <button id="cat-unitFlags" 
                        className="category-pointed"
                        onClick={(e) => this.onPointed(e.target.id)}>
                            Unit Flags
                    </button>
                    <button id="cat-price" 
                        className="category"
                        onClick={(e) => this.onPointed(e.target.id)}>
                            Price
                    </button>
                </div>
    
            </div>
        )
    }
}

export default HomePage;