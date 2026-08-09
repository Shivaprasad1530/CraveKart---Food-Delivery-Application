<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.craveKart.model.Menu,com.craveKart.model.OrderItem,com.craveKart.model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
	User user = (User) session.getAttribute("user");
	List<Menu> cartMenus = (List<Menu>) request.getAttribute("cartMenus");
	List<OrderItem> cartOrderItems = (List<OrderItem>) request.getAttribute("cartOrderItems");
	Integer subtotal = (Integer) request.getAttribute("subtotal");
	if (subtotal == null) subtotal = 0;
	String savedAddress = (user != null && user.getAddress() != null) ? user.getAddress() : "";
	pageContext.setAttribute("savedAddress", savedAddress); // expose to EL — ${savedAddress} can't see a raw scriptlet variable
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Cravekart — Checkout</title>
<meta name="description" content="Confirm your address, apply a coupon, and place your order on Cravekart.">
<link rel="stylesheet" href="<%= request.getContextPath() %>/home.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/menu.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/cart.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/checkout.css">
</head>
<body>

<!-- ============ NAV (identical to menu.jsp / cart.jsp) ============ -->
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
  <div class="nav-actions"></div>
</header>

<!-- ============ CHECKOUT HERO ============ -->
<section class="menu-hero cart-hero checkout-hero">
  <div class="menu-hero-bg cart-hero-bg" aria-hidden="true"></div>
  <div class="menu-hero-content">
    <a class="back-link" href="<%= request.getContextPath() %>/Cart">&larr; Back to Cart</a>
    <p class="eyebrow">Cravekart · Final Step</p>
    <h1>Almost <em>there.</em></h1>
    <p class="lead">Confirm where it's going, apply an offer if you've got one, and choose how you'll pay.</p>
  </div>
</section>

<section class="menu-section checkout-section">
  <div class="checkout-grid">

    <!-- ============ LEFT COLUMN: address + summary ============ -->
    <div class="checkout-col">

      <!-- ---- Address card ---- -->
      <div class="menu-receipt checkout-card">
        <div class="receipt-head">
          <p class="kicker">Delivery To</p>
          <h2>Your address</h2>
          <div class="receipt-barcode" aria-hidden="true"></div>
        </div>
        <div class="checkout-address-body">
          <c:if test="${empty savedAddress}">
            <p class="address-missing-note">We don't have an address saved yet — please add one to continue.</p>
          </c:if>
          <textarea id="addressInput" class="address-textarea" rows="3"
                    placeholder="House no, street, area, city, pincode..."><c:out value="${savedAddress}"/></textarea>
          <p class="address-hint">This will be saved to your profile for next time. Edit it anytime before confirming.</p>
        </div>
      </div>

      <!-- ---- Order summary card ---- -->
      <div class="menu-receipt checkout-card">
        <div class="receipt-head">
          <p class="kicker">Order Summary</p>
          <h2>What you're getting</h2>
          <div class="receipt-barcode" aria-hidden="true"></div>
        </div>

        <c:choose>
          <c:when test="${empty cartMenus}">
            <div class="m-empty">
              <h3>Your cart is empty</h3>
              <p>Head back and add something before checking out.</p>
            </div>
          </c:when>
          <c:otherwise>
            <c:forEach var="menuItem" items="${cartMenus}" varStatus="st">
              <c:set var="orderItem" value="${cartOrderItems[st.index]}" />
              <div class="checkout-line-row">
                <span class="checkout-line-name">${menuItem.name} <span class="checkout-line-qty">&times; ${orderItem.quantity}</span></span>
                <span class="checkout-line-amt">&#8377;${orderItem.item_total}</span>
              </div>
            </c:forEach>

            <!-- ---- Coupon block ---- -->
            <div class="coupon-block">
              <input type="text" id="couponInput" class="coupon-input" placeholder="Have a coupon? Try REDUCE100" />
              <button type="button" id="couponApplyBtn" class="coupon-apply-btn" onclick="applyCoupon()">Apply</button>
            </div>
            <p id="couponMessage" class="coupon-message"></p>

            <div class="cart-total-row">
              <span class="cart-total-label">Subtotal</span>
              <span class="cart-total-amt" id="subtotalAmt">&#8377;${subtotal}</span>
            </div>
            <div class="cart-total-row discount-row" id="discountRow" style="display:none;">
              <span class="cart-total-label">Coupon discount</span>
              <span class="cart-total-amt discount-amt">&minus;&#8377;<span id="discountAmt">0</span></span>
            </div>
            <div class="cart-total-row grand-total-row">
              <span class="cart-total-label">To Pay</span>
              <span class="cart-total-amt" id="grandTotalAmt">&#8377;${subtotal}</span>
            </div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>

    <!-- ============ RIGHT COLUMN: payment ============ -->
    <c:if test="${not empty cartMenus}">
    <div class="checkout-col">
      <div class="menu-receipt checkout-card payment-card">
        <div class="receipt-head">
          <p class="kicker">Payment</p>
          <h2>How will you pay?</h2>
          <div class="receipt-barcode" aria-hidden="true"></div>
        </div>

        <div class="payment-options">
          <label class="payment-option" data-mode="COD">
            <input type="radio" name="paymentMode" value="COD" onchange="selectPaymentMode('COD')">
            <span class="payment-option-label">Cash on Delivery</span>
          </label>
          <label class="payment-option" data-mode="UPI">
            <input type="radio" name="paymentMode" value="UPI" onchange="selectPaymentMode('UPI')">
            <span class="payment-option-label">UPI</span>
          </label>
        </div>

        <div id="upiAppRow" class="upi-app-row" style="display:none;">
          <button type="button" class="upi-app-btn" data-app="PhonePe" onclick="selectUpiApp(this)">PhonePe</button>
          <button type="button" class="upi-app-btn" data-app="GPay" onclick="selectUpiApp(this)">Google Pay</button>
          <button type="button" class="upi-app-btn" data-app="Paytm" onclick="selectUpiApp(this)">Paytm</button>
        </div>

        <button type="button" id="confirmOrderBtn" class="btn-solid cart-checkout-btn" disabled onclick="confirmOrder()">
          Confirm Order
        </button>
        <p id="confirmError" class="confirm-error"></p>
      </div>
    </div>
    </c:if>

  </div>
</section>

<!-- ============ ORDER CONFIRMED OVERLAY ============ -->
<div id="orderConfirmedOverlay" class="order-confirmed-overlay">
  <div class="order-confirmed-card">
    <svg class="confirm-check" viewBox="0 0 80 80">
      <circle class="confirm-check-circle" cx="40" cy="40" r="36" />
      <path class="confirm-check-mark" d="M22 41 L34 53 L58 27" />
    </svg>
    <h2>Your order is confirmed!</h2>
    <p>The food will be delivered soon.</p>
  </div>
</div>

<script>
  var ctx = "<%= request.getContextPath() %>";
  var subtotalValue = ${subtotal};
  var discountValue = 0;
  var selectedPaymentMode = null;
  var selectedUpiApp = null;

  function applyCoupon() {
    var code = document.getElementById('couponInput').value;
    var msgEl = document.getElementById('couponMessage');
    var btn = document.getElementById('couponApplyBtn');
    btn.disabled = true;

    fetch(ctx + "/ApplyCoupon?code=" + encodeURIComponent(code))
      .then(function(r){ return r.json(); })
      .then(function(data){
        btn.disabled = false;
        msgEl.textContent = data.message || '';
        msgEl.className = 'coupon-message ' + (data.applied ? 'coupon-success' : 'coupon-error');

        discountValue = data.discount || 0;
        var discountRow = document.getElementById('discountRow');
        if (data.applied) {
          discountRow.style.display = 'flex';
          document.getElementById('discountAmt').textContent = data.discount;
        } else {
          discountRow.style.display = 'none';
        }
        document.getElementById('grandTotalAmt').textContent = '\u20B9' + data.finalTotal;
      })
      .catch(function(){
        btn.disabled = false;
        msgEl.textContent = 'Something went wrong applying the coupon.';
        msgEl.className = 'coupon-message coupon-error';
      });
  }

  function selectPaymentMode(mode) {
    selectedPaymentMode = mode;
    selectedUpiApp = null;

    document.querySelectorAll('.payment-option').forEach(function(el){
      el.classList.toggle('is-selected', el.getAttribute('data-mode') === mode);
    });

    var upiRow = document.getElementById('upiAppRow');
    if (mode === 'UPI') {
      upiRow.style.display = 'flex';
      document.querySelectorAll('.upi-app-btn').forEach(function(b){ b.classList.remove('is-selected'); });
    } else {
      upiRow.style.display = 'none';
    }
    updateConfirmButtonState();
  }

  function selectUpiApp(btn) {
    selectedUpiApp = btn.getAttribute('data-app');
    document.querySelectorAll('.upi-app-btn').forEach(function(b){ b.classList.remove('is-selected'); });
    btn.classList.add('is-selected');
    updateConfirmButtonState();
  }

  function updateConfirmButtonState() {
    var ready = selectedPaymentMode === 'COD' || (selectedPaymentMode === 'UPI' && selectedUpiApp);
    document.getElementById('confirmOrderBtn').disabled = !ready;
  }

  function confirmOrder() {
    var address = document.getElementById('addressInput').value.trim();
    var errorEl = document.getElementById('confirmError');
    errorEl.textContent = '';

    if (!address) {
      errorEl.textContent = 'Please enter a delivery address.';
      return;
    }
    if (!selectedPaymentMode) {
      errorEl.textContent = 'Please select a payment method.';
      return;
    }
    if (selectedPaymentMode === 'UPI' && !selectedUpiApp) {
      errorEl.textContent = 'Please select a UPI app.';
      return;
    }

    var btn = document.getElementById('confirmOrderBtn');
    btn.disabled = true;
    btn.textContent = 'Confirming...';

    var params = new URLSearchParams();
    params.set('address', address);
    params.set('paymentMode', selectedPaymentMode);
    if (selectedUpiApp) params.set('upiApp', selectedUpiApp);

    fetch(ctx + "/ConfirmOrder?" + params.toString(), {
      method: 'POST'
    })
      .then(function(r){ return r.json().then(function(data){ return {ok: r.ok, data: data}; }); })
      .then(function(res){
        if (!res.ok || res.data.error) {
          btn.disabled = false;
          btn.textContent = 'Confirm Order';
          errorEl.textContent = res.data.error || 'Something went wrong confirming your order.';
          return;
        }
        showOrderConfirmed(res.data.orderId);
      })
      .catch(function(){
        btn.disabled = false;
        btn.textContent = 'Confirm Order';
        errorEl.textContent = 'Network error — please try again.';
      });
  }

  function showOrderConfirmed(orderId) {
    var overlay = document.getElementById('orderConfirmedOverlay');
    overlay.classList.add('is-visible');
    setTimeout(function () {
      window.location.href = ctx + "/OrderStatus?o_id=" + orderId;
    }, 3200);
  }
</script>

</body>
</html>
