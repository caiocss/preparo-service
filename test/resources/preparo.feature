Feature: Receive a pedido and create a preparo with status em preparo

  Scenario: Receive a pedido and create a preparo with status em preparo
    Given I consume a new pedido from pedido.novo-preparo subscription
    When I insert a new preparo with status em preparo
    Then I should publish preparo with status em preparo to pedido.status subscription
