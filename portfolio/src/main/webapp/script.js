// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello There', 'My favorite food is Pizza!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerHTML = "<p style=\"color:white\"> " + greeting + "</p>";
}

async function getMessage() {
  const response = await fetch('/comments');
  const quote = await response.json();
  const json = JSON.stringify(quote)
  document.getElementById('data-container').innerHTML = json +"<br>";
}

function loadComments() {
  fetch('/list-comments').then(response => response.json()).then((commentlist) => {
    const commentListElement = document.getElementById('comment-list');
    commentlist.forEach((commentcontainer) => {
      commentListElement.appendChild(createCommentElement(commentcontainer));
    })
  });
}

/** Creates an element that represents a task, including its delete button. */
function createCommentElement(commentcontainer) {
  const squareElement = document.createElement('li');
  squareElement.className = 'comment';

  const commentElement = document.createElement('span');
  commentElement.innerHTML= "<h4>"+commentcontainer.name + ' '+commentcontainer.lastname + "</h4>";
  commentElement.innerHTML =commentElement.innerHTML + commentcontainer.comment;
  
  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteComment(commentcontainer);

    // Remove the task from the DOM.
    squareElement.remove();
  });

  squareElement.appendChild(commentElement);
  squareElement.appendChild(deleteButtonElement);
  return squareElement;
}

function deleteComment(commentcontainer) {
  const params = new URLSearchParams();
  params.append('id', commentcontainer.id);
  fetch('/delete-comments', {method: 'POST', body: params});
}

/** Creates a map and adds it to the page. */
function createMap() {

  var myLatLng = {lat: 33.882931, lng: -117.8865506};
  
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: myLatLng, 
      zoom: 16,
      mapTypeId: "satellite"
    });

  var marker = new google.maps.Marker({
    position: myLatLng,
    map: map,
    title: 'CSUF!',
    icon: '/images/fullymarker.png'
  });
}