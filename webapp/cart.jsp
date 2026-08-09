<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.craveKart.model.Menu,com.craveKart.model.OrderItem,com.craveKart.model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
	User user = (User) session.getAttribute("user");
	List<Menu> cartMenus = (List<Menu>) request.getAttribute("cartMenus");
	List<OrderItem> cartOrderItems = (List<OrderItem>) request.getAttribute("cartOrderItems");
	Integer cartTotal = (Integer) request.getAttribute("cartTotal");
	if (cartTotal == null) cartTotal = 0;
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Cravekart — Your Cart</title>
<meta name="description" content="Review your order before checkout, on Cravekart.">
<link rel="stylesheet" href="<%= request.getContextPath() %>/home.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/menu.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/cart.css">
</head>
<body>

<!-- ============ NAV (identical to menu.jsp) ============ -->
<header class="navbar">
  <a class="nav-brand" href="<%= request.getContextPath() %>/restaurant.jsp">
    <svg width="30" height="30" viewBox="0 0 40 40" fill="none" role="img" aria-label="Cravekart logo">
      <path d="M10 16h20l-2 16.5a3 3 0 0 1-3 2.7H15a3 3 0 0 1-3-2.7L10 16Z" fill="#FF5A3C"/>
      <path d="M14 16v-2a6 6 0 0 1 12 0v2" stroke="#FF5A3C" stroke-width="2.4" stroke-linecap="round"/>
      <path d="M16 8c0-1.4 1.5-1.4 1.5-2.8S16 2.4 16 2.4" stroke="#FFB627" stroke-width="1.5" stroke-linecap="round"/>
      <path d="M20.2 8c0-1.4 1.5-1.4 1.5-2.8S20.2 2.4 20.2 2.4" stroke="#FFB627" stroke-width="1.5" stroke-linecap="round"/>
      <path d="M24.4 8c0-1.4 1.5-1.4 1.5-2.8S24.4 2.4 24.4 2.4" stroke="#FFB627" stroke-width="1.5" stroke-linecap="round"/>
    </svg>
    <span>Cravekart</span>
  </a>

  <ul class="nav-links">
    <li><a href="<%= request.getContextPath() %>/restaurant.jsp#restaurants">Restaurants</a></li>
    <li><a href="<%= request.getContextPath() %>/restaurant.jsp#offers">Offers</a></li>
    <li><a href="<%= request.getContextPath() %>/restaurant.jsp#about">About</a></li>
  </ul>

  <div class="nav-actions">
  <%if(user == null) {%>
    <a class="btn-ghost" href="login.html">Log In</a>
    <a class="btn-solid" href="signup.html">Sign Up</a>
	<%}%>
  </div>
</header>

<!-- ============ CART HERO ============ -->
<section class="menu-hero cart-hero">
  <div class="menu-hero-bg cart-hero-bg" aria-hidden="true"></div>
  <div class="menu-hero-content">
    <a class="back-link" href="javascript:history.back()">&larr; Back to Menu</a>
    <p class="eyebrow">Cravekart · Checkout</p>
    <h1>Your <em>cart.</em></h1>
    <p class="lead">Double check what you're ordering before you send it to the kitchen.</p>
  </div>
</section>

<!-- ============ CART RECEIPT ============ -->
<section class="menu-section">
  <div class="menu-receipt cart-receipt">

    <div class="receipt-head">
      <p class="kicker">Order Summary</p>
      <h2>Your dishes</h2>
      <div class="receipt-barcode" aria-hidden="true"></div>
    </div>

    <c:choose>
      <c:when test="${empty cartMenus}">
        <div class="m-empty">
          <h3>Your cart is empty</h3>
          <p>Looks like you haven't added anything yet — head back to the menu and pick something tasty.</p>
          <a class="btn-solid cart-empty-btn" href="<%= request.getContextPath() %>/restaurant.jsp">Browse Restaurants</a>
        </div>
      </c:when>

      <c:otherwise>
        <c:forEach var="menuItem" items="${cartMenus}" varStatus="st">
          <c:set var="orderItem" value="${cartOrderItems[st.index]}" />
          <div class="m-row" data-me-id="${menuItem.m_id}">
            <div class="m-thumb">
              <img src="<%= request.getContextPath() %>/${menuItem.imagePath}" alt="${menuItem.name}"
                   onerror="this.onerror=null;this.src='<%= request.getContextPath() %>/images/default-food.png';">
            </div>

            <div class="m-info">
              <h3>${menuItem.name}</h3>
              <p class="m-desc">${menuItem.desc}</p>
            </div>

            <div class="m-side">
              <span class="m-price">${orderItem.item_total}</span>
              <div class="m-qty-control" data-me-id="${menuItem.m_id}">
                <div class="m-stepper" style="display:flex;">
                  <button type="button" class="m-step-btn" onclick="changeQty(this,-1)">&minus;</button>
                  <span class="m-qty-count">${orderItem.quantity}</span>
                  <button type="button" class="m-step-btn" onclick="changeQty(this,1)">+</button>
                </div>
              </div>
            </div>
          </div>
        </c:forEach>

        <div class="cart-total-row">
          <span class="cart-total-label">Total</span>
          <span class="cart-total-amt">&#8377;<span id="grandTotal">${cartTotal}</span></span>
        </div>

        <a class="btn-solid cart-checkout-btn" href="<%= request.getContextPath() %>/Checkout" style="display:block;text-decoration:none;text-align:center;">Proceed to Checkout</a>
      </c:otherwise>
    </c:choose>

  </div>
</section>

<!-- ============ FOOTER (identical to menu.jsp) ============ -->
<footer id="about">
  <div class="footer-top">
    <div class="footer-brand">
      <a class="nav-brand" href="<%= request.getContextPath() %>/restaurant.jsp">
        <svg width="26" height="26" viewBox="0 0 40 40" fill="none" role="img" aria-label="Cravekart logo">
          <path d="M10 16h20l-2 16.5a3 3 0 0 1-3 2.7H15a3 3 0 0 1-3-2.7L10 16Z" fill="#FF5A3C"/>
          <path d="M14 16v-2a6 6 0 0 1 12 0v2" stroke="#FF5A3C" stroke-width="2.4" stroke-linecap="round"/>
        </svg>
        <span>Cravekart</span>
      </a>
      <p>Your city's kitchens, delivered — live tracking, saved favorites, and deals from 2,000+ local restaurants.</p>
    </div>
    <div class="footer-cols">
      <div class="footer-col">
        <h4>Company</h4>
        <ul>
          <li><a href="<%= request.getContextPath() %>/restaurant.jsp#about">About Us</a></li>
          <li><a href="#">Careers</a></li>
          <li><a href="#">Partner with Us</a></li>
        </ul>
      </div>
      <div class="footer-col">
        <h4>Support</h4>
        <ul>
          <li><a href="#">Help Center</a></li>
          <li><a href="#">Track Order</a></li>
          <li><a href="#">Contact Us</a></li>
        </ul>
      </div>
      <div class="footer-col">
        <h4>Legal</h4>
        <ul>
          <li><a href="#">Terms of Service</a></li>
          <li><a href="#">Privacy Policy</a></li>
        </ul>
      </div>
    </div>
  </div>
  <div class="footer-barcode" aria-hidden="true"></div>
  <div class="footer-bottom">
    <span>© <%= java.time.Year.now() %> Cravekart. All rights reserved.</span>
    <span>Made for hungry cities everywhere.</span>
  </div>
</footer>

<script>
  var ctx = "<%= request.getContextPath() %>";

  function changeQty(btn, delta) {
    var wrap = btn.closest('.m-qty-control');
    var meId = wrap.getAttribute('data-me-id');
    var row = wrap.closest('.m-row');
    var action = delta > 0 ? "increment" : "decrement";
    btn.disabled = true;

    fetch(ctx + "/AddToCart?me_id=" + meId + "&action=" + action)
      .then(function(r){ return r.json(); })
      .then(function(data){
        btn.disabled = false;
        if (data.error) { alert(data.error); return; }

        if (data.itemQty === 0) {
          // item removed entirely -> drop the row, reload if cart now empty
          row.remove();
          if (data.cartCount === 0) { window.location.reload(); }
        } else {
          wrap.querySelector('.m-qty-count').textContent = data.itemQty;
          // note: per-row price shown is item_total from load time; a full
          // per-row price refresh would need the servlet to also return the
          // updated item_total, which is easy to add if you want live pricing.
        }

        document.getElementById('grandTotal').textContent = data.cartTotal;
      })
      .catch(function(){ btn.disabled = false; });
  }
</script>

</body>
</html>
