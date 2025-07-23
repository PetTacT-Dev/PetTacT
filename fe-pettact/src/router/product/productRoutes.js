import ProductList from '@/view/product/ProductList.vue';
import ProductDetail from '@/view/product/ProductDetail.vue';
import ProductCreate from '@/view/product/ProductCreate.vue';
import ProductUpdate from '@/view/product/ProductUpdate.vue';
import Cart from '@/view/cart/Cart.vue';
import OrderList from '@/view/order/OrderList.vue';
import OrderDetail from '@/view/order/OrderDetail.vue';
import OrderCreate from '@/view/order/OrderCreate.vue';
import OrderSheet from '@/view/order/OrderSheet.vue';
import OrderPayment from '@/view/order/OrderPayment.vue';
import PaymentProcessing from '@/view/order/PaymentProcessing.vue';
import PaymentSuccess from '@/view/order/PaymentSuccess.vue';
import PaymentFail from '@/view/order/PaymentFail.vue';

export default [
  { path: '/product', name: 'ProductList', component: ProductList },
  { path: '/product/create', name: 'ProductCreate', component: ProductCreate },
  { path: '/product/:id', name: 'ProductDetail', component: ProductDetail, props: true },
  { path: '/product/:id/update', name: 'ProductUpdate', component: ProductUpdate, props: true },
  { path: '/cart', name: 'Cart', component: Cart },
  { path: '/order', name: 'OrderList', component: OrderList },
  { path: '/order/:orderNo', name: 'OrderDetail', component: OrderDetail },
  { path: '/order/create', name: 'OrderCreate', component: OrderCreate },
  { path: '/order-sheet', name: 'OrderSheet', component: OrderSheet },
  { path: '/order/payment', name: 'OrderPayment', component: OrderPayment },
  { path: '/order/payment-success', name: 'PaymentProcessing', component: PaymentProcessing },
  { path: '/order/payment-complete', name: 'PaymentSuccess', component: PaymentSuccess },
  { path: '/order/payment-fail', name: 'PaymentFail', component: PaymentFail },
]