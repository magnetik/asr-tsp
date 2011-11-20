  $(document).ready(function () {
      $('#addUser').click(function () {
          $.ajax({
              url: 'http://localhost:8088/jersey-demo/directory/add/' + $('#addUserName').val(),
              type: 'GET',
              dataType: "xml",
              statusCode: {
                  201: function () {
                      alert('User Created ! :-)');
                  },
                  400: function () {
                      alert('Error ! :-(');
                  }
              }
          });
      });
      $('#toUpdate').click(function () {
          $.ajax({
              url: 'http://localhost:8088/jersey-demo/directory/listUsers',
              type: 'GET',
              dataType: "xml",
              statusCode: {
                  200: function (xml) {
                      $('#addMessageTo').empty();
                      $('#addMessageTo').append('<optgroup label="Users">');
                      $(xml).find('Item').each(function () {
                          $('#addMessageTo').append('<option>' + $(this).text() + '</option>');
                      });
                      $('#addMessageTo').append('</optgroup>');
                  }
              }
          });
          $.ajax({
              url: 'http://localhost:8088/jersey-demo/directory/user/' + $('#MyName').val() + '/writable',
              type: 'GET',
              dataType: "xml",
              statusCode: {
                  200: function (xml) {
                      $('#addMessageTo').append('<optgroup label="News box">');
                      $(xml).find('Item').each(function () {

                          $('#addMessageTo').append('<option>' + $(this).text() + '</option>');

                      });
                      $('#addMessageTo').append('</optgroup>');
                  }
              }
          });
      });
      $('#addMessage').click(function () {
          $.ajax({
              url: 'http://localhost:8088/jersey-demo/mbm/box',
              type: 'POST',
              data: "receiverName=" + $('#addMessageTo').val() + "&senderName=" + $('#MyName').val() + "&subject=" + $('#addMessageSubject').val() + "&body=" + $('#addMessageBody').val(),
              statusCode: {
                  201: function () {
                      alert('Message sent !');
                  },
                  401: function () {
                      alert('Unauthorized');
                  },
                  404: function () {
                      alert('Box unknown');
                  }
              }
          });
      });
      $('#getMessages').click(function () {
          $.ajax({
              url: 'http://localhost:8088/jersey-demo/mbm/box/' + $('#MyName').val(),
              type: 'GET',
              dataType: "xml",
              statusCode: {
                  200: function (xml) {
                      $('#Messages').empty();
                      $('#MessagesBox').empty();
                      $('#MessagesBox').append(" " + $('#MyName').val());
                      $(xml).find('message').each(function () {
                          // Iterate on all messages
                          var from = $(this).find('senderName').text();
                          var body = $(this).find('body').text();
                          var subject = $(this).find('subject').text();
                          var status = $(this).find('status').text()
                          var msgDate = $(this).find('sendingDate').text()

                          var id = $(this).find('id').text()

                          var deleteText = "<input type='button' id='delete_" + id + "' value='Delete'/>";
                          if (status == "New") {
                              var statusText = "<input type='button' id='read_" + id + "' value='Read'/>";
                          } else {
                              var statusText = "";
                          }
                          // Get vars
                          $('#Messages').append('<hr /><p><b>From:</b>' + from + '<br /><b>Date:</b>' + msgDate + '<br /><b>Subject:</b>' + subject + '<br />' + body + '</p>' + statusText + deleteText + '<hr />');

                      });
                      // Embeded javascript
                      $("input[id^='delete_']").click(function () {
                          var idToDelete = $(this).attr('id').split('_')[1];

                          // Do an ajax request to delete
                          $.ajax({
                              url: 'http://localhost:8088/jersey-demo/mbm/' + idToDelete,
                              type: 'DELETE',
                              dataType: "xml",
                              statusCode: {
                                  410: function () {
                                      alert('message deleted !!');
                                  },
                                  404: function () {
                                      alert('Not found');
                                  }
                              }
                          });

                      });
                      $("input[id^='read_']").click(function () {
                          var idToRead = $(this).attr('id').split('_')[1];

                          // Do an ajax request to read
                          $.ajax({
                              url: 'http://localhost:8088/jersey-demo/mbm/read/' + idToRead,
                              type: 'PUT',
                              dataType: "xml",
                              statusCode: {
                                  200: function () {
                                      alert('message read !!');
                                  },
                                  404: function () {
                                      alert('Message not found :-(');
                                  }
                              }
                          });

                      });
                  },
                  404: function () {
                      alert('Box unknown');
                  }
              }
          });
          // Get readable box for this user
          $.ajax({
              url: 'http://localhost:8088/jersey-demo/directory/user/' + $('#MyName').val() + '/readable',
              type: 'GET',
              dataType: "xml",
              statusCode: {
                  200: function (xml) {
                      $(xml).find('Item').each(function () {
                          var boxname = $(this).text();
                          $('#MessagesBox').append(" ; " + $(this).text());
                          // We have found a new box with the name boxname
                          // Let's get its messages
                          $.ajax({
                              url: 'http://localhost:8088/jersey-demo/mbm/box/' + boxname,
                              type: 'GET',
                              dataType: "xml",
                              statusCode: {
                                  200: function (xml) {
                                      $(xml).find('message').each(function () {
                                          // Iterate on all messages
                                          var from = $(this).find('senderName').text();
                                          var body = $(this).find('body').text();
                                          var subject = $(this).find('subject').text();
                                          var status = $(this).find('status').text()
                                          var msgDate = $(this).find('sendingDate').text()

                                          var id = $(this).find('id').text()

        
                                          // Get vars
                                          $('#Messages').append('<hr /><p><b>From (newsBox):</b>' + from + '<br /><b>Date:</b>' + msgDate + '<br /><b>Subject:</b>' + subject + '<br />' + body + '</p><hr />');

                                      });
                                  },
                                  404: function () {
                                      alert('Box unknown');
                                  }
                              }
                          });

                      });
                  }
              }
          });
      });
  });