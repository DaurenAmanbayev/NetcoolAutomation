<%
    if (session.getAttribute("AUTH") != null && ((Boolean) session.getAttribute("AUTH"))) {
    } else {
        //not logged.. bye bye...
        response.sendRedirect("login.html");
    }
%>