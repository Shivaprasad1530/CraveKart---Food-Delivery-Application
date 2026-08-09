<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.craveKart.model.User" %>
<%@ page import="com.craveKart.Controller.DeliveryDashboard.DeliveryOrderView" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
	User user = (User) session.getAttribute("user");
	DeliveryOrderView activeDelivery = (DeliveryOrderView) request.getAttribute("activeDelivery");
	List<DeliveryOrderView> availableOrders = (List<DeliveryOrderView>) request.getAttribute("availableOrders");
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Cravekart — Delivery Dashboard</title>
<meta name="description" content="Accept and manage deliveries on Cravekart.">
<link rel="stylesheet" href="<%= request.getContextPath() %>/home.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/menu.css">

<style>
/* =========================================================
   Delivery Dashboard — same receipt/paper theme as
   restaurantDashboard.jsp, embedded here the same way.
   ========================================================= */

.dash-hero{
  position: relative;
  min-height: 28vh;
  display: flex;
  align-items: flex-end;
  overflow: hidden;
  padding: 3rem clamp(1.25rem, 6vw, 4rem) 5rem;
}
.dash-hero-bg{
  position: absolute;
  inset: 0;
  z-index: 0;
  background-image:
    linear-gradient(180deg, rgba(20,12,9,.4) 0%, rgba(18,11,8,.88) 68%, var(--bg-void) 100%),
    url("https://images.unsplash.com/photo-1526367790999-0150786686a2?auto=format&fit=crop&w=1920&q=80");
  background-size: cover;
  background-position: center 45%;
}
.dash-hero-content{ position: relative; z-index: 1; max-width: 640px; }
.dash-hero .eyebrow{
  font-family: 'JetBrains Mono', monospace;
  font-size: .72rem;
  font-weight: 700;
  letter-spacing: .16em;
  text-transform: uppercase;
  color: var(--turmeric);
  margin: 0 0 .8rem;
}
.dash-hero h1{
  font-family: 'Fraunces', serif;
  font-weight: 600;
  font-size: clamp(2rem, 4.4vw, 3rem);
  line-height: 1.05;
  margin: 0 0 .5rem;
}
.dash-hero p{
  color: var(--cream);
  opacity: .8;
  font-size: .92rem;
  margin: 0;
}

.dash-section{
  background: var(--bg-section);
  padding: 0 clamp(1.25rem, 6vw, 4rem) 3rem;
}
.dash-panel{ margin: -3.5rem auto 0; }

/* ---------- delivery card ---------- */

.delivery-card{
  padding: 1.3rem 0;
  border-bottom: 2px dashed rgba(36,22,17,.16);
}
.delivery-card:last-child{ border-bottom: none; }

.delivery-card-id{
  font-family: 'JetBrains Mono', monospace;
  font-weight: 800;
  font-size: .8rem;
  color: var(--ink-soft);
  margin-bottom: .8rem;
  display: block;
}

.stop-block{
  display: flex;
  gap: .9rem;
  padding: .7rem 0;
}
.stop-icon{
  flex: 0 0 auto;
  width: 30px; height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'JetBrains Mono', monospace;
  font-weight: 800;
  font-size: .72rem;
  color: #fff;
}
.stop-icon.pickup{ background: var(--chili); }
.stop-icon.dropoff{ background: #4C8B2B; }

.stop-info{ flex: 1 1 auto; min-width: 0; }
.stop-label{
  font-family: 'JetBrains Mono', monospace;
  font-size: .65rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: .05em;
  color: var(--ink-soft);
  margin: 0 0 .15rem;
}
.stop-name{
  font-family: 'Fraunces', serif;
  font-weight: 600;
  font-size: 1rem;
  color: var(--ink);
  margin: 0 0 .2rem;
}
.stop-address{
  font-size: .82rem;
  color: var(--ink-soft);
  margin: 0 0 .2rem;
}
.stop-phone{
  font-family: 'JetBrains Mono', monospace;
  font-size: .8rem;
  color: var(--ink);
}

.stage-note{
  font-family: 'Plus Jakarta Sans', sans-serif;
  font-size: .85rem;
  color: var(--ink-soft);
  font-style: italic;
  margin: .9rem 0 0;
  padding: .6rem .8rem;
  background: rgba(255,90,60,.06);
  border-radius: 8px;
}

.delivery-action-btn{
  display: block;
  width: 100%;
  margin-top: 1rem;
  font-family: 'Plus Jakarta Sans', sans-serif;
  font-weight: 800;
  font-size: .82rem;
  letter-spacing: .02em;
  text-transform: uppercase;
  color: #fff;
  background: var(--chili);
  border: none;
  border-radius: 8px;
  padding: .75rem 1rem;
  cursor: pointer;
  transition: background .15s ease, transform .12s ease;
}
.delivery-action-btn:hover{ background: #E14A2E; transform: translateY(-1px); }
.delivery-action-btn:disabled{ opacity: .5; cursor: not-allowed; transform: none; }
</style>
</head>
<body>

<!-- ============ NAV ============ -->
<header class="navbar">
  <a class="nav-brand" href="<%= request.getContextPath() %>/deliveryDashboard">
    <svg width="30" height="30" viewBox="0 0 40 40" fill="none" role="img" aria-label="Cravekart logo">
      <path d="M10 16h20l-2 16.5a3 3 0 0 1-3 2.7H15a3 3 0 0 1-3-2.7L10 16Z" fill="#FF5A3C"/>
      <path d="M14 16v-2a6 6 0 0 1 12 0v2" stroke="#FF5A3C" stroke-width="2.4" stroke-linecap="round"/>
    </svg>
    <span>Cravekart <small style="opacity:.6;font-weight:600;">Rider</small></span>
  </a>
  
</header>

<!-- ============ HERO ============ -->
<section class="dash-hero">
  <div class="dash-hero-bg" aria-hidden="true"></div>
  <div class="dash-hero-content">
    <p class="eyebrow">Cravekart · Delivery Dashboard</p>
    <h1>On the road.</h1>
    <c:choose>
      <c:when test="${activeDelivery != null}">
        <p>You have one active delivery — finish it up before taking another.</p>
      </c:when>
      <c:otherwise>
        <p>Accept an order below to start your next delivery.</p>
      </c:otherwise>
    </c:choose>
  </div>
</section>

<section class="dash-section">
  <div class="menu-receipt dash-panel">

    <c:choose>

      <%-- ================= ACTIVE DELIVERY ================= --%>
      <c:when test="${activeDelivery != null}">
        <div class="receipt-head">
          <p class="kicker">Current Delivery</p>
          <h2>In progress</h2>
          <div class="receipt-barcode" aria-hidden="true"></div>
        </div>

        <div class="delivery-card">
          <span class="delivery-card-id">Order #${activeDelivery.order.o_id}</span>

          <div class="stop-block">
            <div class="stop-icon pickup">P</div>
            <div class="stop-info">
              <p class="stop-label">Pickup from</p>
              <p class="stop-name">${activeDelivery.restaurantName}</p>
              <p class="stop-address">${activeDelivery.restaurantAddress}</p>
              <p class="stop-phone">${activeDelivery.restaurantPhone}</p>
            </div>
          </div>

          <div class="stop-block">
            <div class="stop-icon dropoff">D</div>
            <div class="stop-info">
              <p class="stop-label">Deliver to</p>
              <p class="stop-name">${activeDelivery.customerName}</p>
              <p class="stop-address">${activeDelivery.customerAddress}</p>
              <p class="stop-phone">${activeDelivery.customerPhone}</p>
            </div>
          </div>

          <c:choose>
            <c:when test="${activeDelivery.order.status == 'rider_confirmed'}">
              <p class="stage-note">Head to the restaurant to pick up this order. They'll mark it dispatched once it's handed to you.</p>
            </c:when>
            <c:when test="${activeDelivery.order.status == 'dispatched'}">
              <button type="button" class="delivery-action-btn" onclick="markArrived(${activeDelivery.order.o_id}, this)">
                Arrived at Delivery Location
              </button>
            </c:when>
            <c:when test="${activeDelivery.order.status == 'reached_location'}">
              <p class="stage-note">Waiting for the customer to confirm they've received the order.</p>
            </c:when>
          </c:choose>
        </div>
      </c:when>

      <%-- ================= AVAILABLE ORDERS ================= --%>
      <c:otherwise>
        <div class="receipt-head">
          <p class="kicker">Ready For Pickup</p>
          <h2>Available orders</h2>
          <div class="receipt-barcode" aria-hidden="true"></div>
        </div>

        <c:choose>
          <c:when test="${empty availableOrders}">
            <div class="m-empty">
              <h3>No orders waiting right now</h3>
              <p>Check back shortly — new deliveries will show up here as restaurants finish preparing them.</p>
            </div>
          </c:when>
          <c:otherwise>
            <c:forEach var="av" items="${availableOrders}">
              <div class="delivery-card">
                <span class="delivery-card-id">Order #${av.order.o_id}</span>

                <div class="stop-block">
                  <div class="stop-icon pickup">P</div>
                  <div class="stop-info">
                    <p class="stop-label">Pickup from</p>
                    <p class="stop-name">${av.restaurantName}</p>
                    <p class="stop-address">${av.restaurantAddress}</p>
                  </div>
                </div>

                <div class="stop-block">
                  <div class="stop-icon dropoff">D</div>
                  <div class="stop-info">
                    <p class="stop-label">Deliver to</p>
                    <p class="stop-name">${av.customerName}</p>
                    <p class="stop-address">${av.customerAddress}</p>
                  </div>
                </div>

                <button type="button" class="delivery-action-btn" onclick="acceptOrder(${av.order.o_id}, this)">
                  Accept Order
                </button>
              </div>
            </c:forEach>
          </c:otherwise>
        </c:choose>
      </c:otherwise>
    </c:choose>

  </div>
</section>

<script>
  var ctx = "<%= request.getContextPath() %>";

  function acceptOrder(o_id, btn) {
    btn.disabled = true;
    fetch(ctx + "/deliveryDashboard?action=acceptOrder&o_id=" + o_id, { method: 'POST' })
      .then(function(r){ return r.json(); })
      .then(function(data){
        if (data.error) { alert(data.error); btn.disabled = false; return; }
        window.location.reload(); // switches into the "active delivery" view
      })
      .catch(function(){ btn.disabled = false; alert('Could not accept this order.'); });
  }

  function markArrived(o_id, btn) {
    btn.disabled = true;
    fetch(ctx + "/deliveryDashboard?action=markArrived&o_id=" + o_id, { method: 'POST' })
      .then(function(r){ return r.json(); })
      .then(function(data){
        if (data.error) { alert(data.error); btn.disabled = false; return; }
        window.location.reload(); // shows the "waiting for customer" stage next
      })
      .catch(function(){ btn.disabled = false; alert('Could not update delivery status.'); });
  }
</script>

</body>
</html>
