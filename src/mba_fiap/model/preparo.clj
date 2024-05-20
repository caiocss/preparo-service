(ns mba-fiap.model.preparo)


(def Produtos
  [:vector {:min 1 :max 3}
   uuid?])


(def em-preparo "em-preparo")
(def pronto "pronto")
(def entregue "entregue")


(def Status
  [:enum
   em-preparo
   pronto
   entregue])


(def Preparo
  [:map
   [:id-pedido uuid?]
   [:id-cliente uuid?]
   [:produtos Produtos]
   [:numero-do-pedido string?]
   [:status Status]
   [:created-at {:optional true} string?]])
