import React, { Component } from 'react';

require('./Comments.css');

class Comments extends Component {
  constructor(props) {
    super(props);
    this.state = {
      comments: [],
    };
  }

  async componentDidMount() {
    try {
      const comments = await this.getComments();
      this.setState({ comments });
    } catch (error) {
      console.error(error);
    }
  }

  getCsrfToken = async () => {
    const response = await fetch('/libs/granite/csrf/token.json');
    const json = await response.json();
    return json.token;
  };

  getComments = async () => {
    const csrfToken = await this.getCsrfToken();
    const response = await fetch('/bin/showcase/comments', {
      method: 'GET',
      headers: {
        'CSRF-Token': csrfToken,
      },
    });

    if (!response.ok) {
      throw new Error('Failed to fetch comments');
    }

    const comments = await response.json();
    return comments;
  };

  render() {
    return (
      <div>
        {this.props.commentTitle && <h1>{this.props.commentTitle}</h1>}
        {this.props.commentTitle && (
          <ul>
            {this.state.comments.map((comment, index) => (
              <li key={index}>{comment.content}</li>
            ))}
          </ul>
        )}
      </div>
    );
  }
}

export default Comments;