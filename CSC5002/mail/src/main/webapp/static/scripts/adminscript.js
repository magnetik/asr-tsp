    $(document).ready(function () {
      $('#addNewsBox').click(function () {
        $.ajax({
          url: 'http://localhost:8088/jersey-demo/mbm/addbox',
          type: 'POST',
          data: "name=" + $('#addNewsBoxName').val() + "&type=News",
          statusCode: {
            201: function () {
              alert('Box created !');
            },
            400: function () {
              alert('Error');
            }
          }
        });
      });
      $('#deleteNewsBox').click(function () {
        $.ajax({
          url: 'http://localhost:8088/jersey-demo/mbm/box/' + $('#deleteNewsBoxName').val(),
          type: 'DELETE',
          dataType: "xml",
          statusCode: {
            410: function () {
              alert('Box Deleted !!');
            },
            404: function () {
              alert('Not found');
            }
          }
        });
      });
      $('#addUser').click(function () {
        $.ajax({
          url: 'http://localhost:8088/jersey-demo/directory/add/' + $('#addUserName').val(),
          type: 'GET',
          dataType: "xml",
          statusCode: {
            201: function () {
              alert('User Created !!');
            },
            400: function () {
              alert('Error');
            }
          }
        });
      });
      $('#deleteUser').click(function () {
        $.ajax({
          url: 'http://localhost:8088/jersey-demo/directory/delete/' + $('#deleteUserName').val(),
          type: 'DELETE',
          dataType: "xml",
          statusCode: {
            410: function () {
              alert('Message deleted !!');
            },
            404: function () {
              alert('Message not found');
            }
          }
        });
      });
      $('#listUserNewsBox').click(function () {
        $.ajax({
          url: 'http://localhost:8088/jersey-demo/directory/listUsers',
          type: 'GET',
          dataType: "xml",
          statusCode: {
            200: function (xml) {
              $('#userList').empty();
              $(xml).find('Item').each(function () {

                $('#userList').append('<option>' + $(this).text() + '</option>');

              });
            }
          }
        });
        $.ajax({
          url: 'http://localhost:8088/jersey-demo/mbm/listNewsBox',
          type: 'GET',
          dataType: "xml",
          statusCode: {
            200: function (xml) {
              $('#newsBoxList').empty();
              $(xml).find('Item').each(function () {

                $('#newsBoxList').append('<option>' + $(this).text() + '</option>');

              });
            }
          }
        });
      });
      $('#getMessages').click(function () {
          $.ajax({
              url: 'http://localhost:8088/jersey-demo/mbm/box/' + $('#getMessageBox').val(),
              type: 'GET',
              dataType: "xml",
              statusCode: {
                  200: function (xml) {
                      $('#Messages').empty();
                      $(xml).find('message').each(function () {
                          // Iterate on all messages
                          var from = $(this).find('senderName').text();
                          var body = $(this).find('body').text();
                          var subject = $(this).find('subject').text();
                          var status = $(this).find('status').text()

                          var id = $(this).find('id').text()

                          var deleteText = "<input type='button' id='delete_" + id + "' value='Delete'/>";
                          if (status == "New") {
                              var statusText = "<input type='button' id='read_" + id + "' value='Read'/>";
                          } else {
                              var statusText = "";
                          }
                          // Get vars
                          $('#Messages').append('<hr /><p><b>From:</b>' + from + '<br /><b>Subject:</b>' + subject + '<br />' + body + '</p>' + statusText + deleteText + '<hr />');

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

                          // Do an ajax request to delete
                          $.ajax({
                              url: 'http://localhost:8088/jersey-demo/mbm/read' + idToDelete,
                              type: 'GET',
                              dataType: "xml",
                              statusCode: {
                                  410: function () {
                                      alert('message read !!');
                                  },
                                  404: function () {
                                      alert('Not found');
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
      });
      $('#updateUserRight').click(function () {
        $.ajax({
          url: 'http://localhost:8088/jersey-demo/directory/updateUser/' + $('#userList').val() + '/' + $('#newsBoxList').val() + '/' + $('#right').val(),
          type: 'PUT',
          dataType: "xml",
          statusCode: {
            200: function () {
              alert('Rights Updated !!');
            },
            400: function () {
              alert('Error');
            }
          }
        });
      });
    });