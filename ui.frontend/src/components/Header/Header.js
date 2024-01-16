import React, { Component } from 'react';

require('./Header.css');

class Header extends Component {
  
  render() {
    return (
      <div className="header-showcase">
        {this.props.logoUrl && <img src={this.props.logoUrl}></img>}
        {this.props.links && (
          <ul>
            {this.props.links.map((link, index) => (
              <li key={index}>
                <a href={link.linkUrl} target="_blank" rel="noreferrer">
                  {link.linkTitle}
                </a>
              </li>
            ))}
          </ul>
        )}
      </div>
    );
  }
}

export default Header;