import sanitizeHtml from 'sanitize-html';
import sanitizeWhiteList from '../sanitize-html.whitelist';

import React, { Component } from 'react';
import extractModelId from '../../utils/extract-model-id';

require('./Footer.css');

class Footer extends Component {
  get richTextContent() {
    return (
      <div
        id={extractModelId(this.props.cqPath)}
        data-rte-editelement
        dangerouslySetInnerHTML={{
          __html: sanitizeHtml(this.props.text, sanitizeWhiteList)
        }}
      />
    );
  }

  get textContent() {
    return <div>{this.props.text}</div>;
  }

  render() {
    return (
      <section className='footer-container'>

        <div className='content-container'>
          <img src={this.props.logoUrl}></img>
          <div className='address'>
            {this.props.richText ? this.richTextContent : this.textContent}
          </div>
        </div>
      </section>
    );
  }
}

export default Footer;