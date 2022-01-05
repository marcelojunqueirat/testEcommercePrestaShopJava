package homepage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import base.BaseTests;
import pages.CarrinhoPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ModalProdutoPage;
import pages.PedidoPage;
import pages.ProdutoPage;
import util.Funcoes;


public class HomePageTests extends BaseTests {
	
	@Test
	public void testContarProdutos_oitoProdutosDiferentes() {
		carregarPaginaInicial();
		assertThat(homePage.contarProdutos(), is(8));
	}
	
	@Test
	public void testValidarCarrinhoZerado_zeroItensNoCarrinho() {
		int produtosNoCarrinho = homePage.obterQuantidadeProdutosNoCarrinho();
		assertThat(produtosNoCarrinho, is(0));
	}
	
	ProdutoPage produtoPage;
	String nomeProdutoProdutoPage;
	
	@Test
	public void testValidarDetalhesDoProduto_descricaoEValorIguais() {
		int indice = 0;
		String nomeProdutoHomePage = homePage.obterNomeProduto(indice);
		String precoProdutoHomePage = homePage.obterPrecoProduto(indice);
		
		System.out.println(nomeProdutoHomePage);
		System.out.println(precoProdutoHomePage);
		
		produtoPage = homePage.clicarProduto(indice);
		
		nomeProdutoProdutoPage = produtoPage.obterNomeProduto();
		String precoProdutoProdutoPage = produtoPage.obterPrecoProduto();
		
		System.out.println(nomeProdutoProdutoPage);
		System.out.println(precoProdutoProdutoPage);
		
		assertThat(nomeProdutoHomePage.toUpperCase(), is(nomeProdutoProdutoPage.toUpperCase()));
		assertThat(precoProdutoHomePage, is(precoProdutoProdutoPage));
	}
	
	LoginPage loginPage;
	
	@Test
	public void testLoginComSucesso_UsuarioLogado() {
		// Click no botao Sign In na home Page
		loginPage = homePage.clicarBotaoSignIn();
		
		// Preencher Usuario e Senha
		loginPage.preencherEmail("marcelo@teste.com");
		loginPage.preencherPassword("marcelo");
		
		// Click no Bot�o Sign In para Logar
		loginPage.clicarBotaoSignIn();
		
		// Validar se o usuario esta logado de fato
		assertThat(homePage.estaLogado("Marcelo Bittencourt"), is(true));
		
		carregarPaginaInicial();
	}
	
	
	/*EXEMPLO DE TESTE PARAMETRIZADO COM .CSV IMPORTADO DA RESOURCES*/
	@ParameterizedTest
	@CsvFileSource(resources = "/massaTeste_Login.csv", numLinesToSkip = 1, delimiter = ';')
	public void testLogin_UsuarioLogadoComDadosValidos(String nomeTeste, String email, String password, String nomeUsuario, String resultado) {
		// Click no botao Sign In na home Page
		loginPage = homePage.clicarBotaoSignIn();
		
		// Preencher Usuario e Senha
		loginPage.preencherEmail(email);
		loginPage.preencherPassword(password);
		
		// Click no Bot�o Sign In para Logar
		loginPage.clicarBotaoSignIn();
		
		boolean esperado_loginOk;
		if (resultado.equals("positivo"))
			esperado_loginOk = true;
		else
			esperado_loginOk = false;
		
		// Validar se o usuario esta logado de fato
		assertThat(homePage.estaLogado(nomeUsuario), is(esperado_loginOk));
		
		capturarTela(nomeTeste, resultado);
		
		if (esperado_loginOk)
			homePage.clicarBotaoSignOut();
			
		carregarPaginaInicial();	
	}
	
	ModalProdutoPage modalProdutoPage;
	
	@Test
	public void testIncluirProdutoNoCarrinho_produtoIncluidoComSucesso() {
		
		String tamanhoProduto = "M";
		String corProduto = "Black";
		int quantidadeProduto = 2;
		
		// Pr�-condicao: Usuario Logado
		if(!homePage.estaLogado("Marcelo Bittencourt")) {
			testLoginComSucesso_UsuarioLogado();
		}
		
		// Teste: Selecionando o produto
		testValidarDetalhesDoProduto_descricaoEValorIguais();
		
		// Selecionar tamanho
		List<String> listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		System.out.println(listaOpcoes);
		
		produtoPage.selecionarOpcaoDropDown(tamanhoProduto);

		listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		System.out.println(listaOpcoes);
		
		// Selecionar cor
		produtoPage.selecionarCorPreta();
		
		// Selecionar Quantidade
		produtoPage.alterarQuantidade(quantidadeProduto);
		
		// Adicionar ao carrinho
		modalProdutoPage = produtoPage.clicarBotaoAddToCart();
		
		// Validacoes
		assertTrue(modalProdutoPage.obterMensagemProdutoAdicionado().endsWith("Product successfully added to your shopping cart"));
				
		assertThat(modalProdutoPage.obterDescricaoProduto().toUpperCase(), is(nomeProdutoProdutoPage.toUpperCase()));
		
		String precoProdutoString = modalProdutoPage.obterPrecoProduto();
		precoProdutoString = precoProdutoString.replace("$", "");
		Double precoProduto = Double.parseDouble(precoProdutoString);
		
		assertThat(modalProdutoPage.obterTamanhoProduto(), is(tamanhoProduto));
		assertThat(modalProdutoPage.obterCorProduto(), is(corProduto));
		assertThat(modalProdutoPage.obterQuantidadeProduto(), is(Integer.toString(quantidadeProduto)));
		
		String subTotalString = modalProdutoPage.obterSubtotal();
		subTotalString = subTotalString.replace("$", "");
		Double subTotal = Double.parseDouble(subTotalString);
		
		Double subTotalCalculado = quantidadeProduto * precoProduto;
		assertThat(subTotal, is(subTotalCalculado));
		
	}
	
	// Valores Esperados
	
	String esperado_nomeProduto = "Hummingbird printed t-shirt";
	Double esperado_precoProduto = 19.12;
	String esperado_tamanhoProduto = "M";
	String esperado_corProduto = "Black";
	int esperado_inputQuantidadeProduto = 2;
	Double esperado_subtotalProduto = esperado_precoProduto * esperado_inputQuantidadeProduto;
	
	int esperado_numeroItensTotal = esperado_inputQuantidadeProduto;
	Double esperado_subTotalTotal = esperado_subtotalProduto;
	Double esperado_shippingTotal = 7.00;
	Double esperado_totalTaxExclTotal = esperado_subTotalTotal + esperado_shippingTotal;
	Double esperado_totalTaxIncTotal = esperado_totalTaxExclTotal;
	Double esperado_taxesTotal = 0.00;
	
	String esperado_nomeCliente = "Marcelo Bittencourt";
	
	CarrinhoPage carrinhoPage;
	
	@Test
	public void testIrParaCarrinho_InformacoesPersistidas() {
		// Produto incluido na tela ModalProdutoPage
		testIncluirProdutoNoCarrinho_produtoIncluidoComSucesso();
		carrinhoPage = modalProdutoPage.clicarBotaoProceedToCheckout();
		
		// Teste come�a a partir daqui
		// Validar todos os elementos da tela
		System.out.println("*** TELA DO CARRINHO ***");
		System.out.println(carrinhoPage.obter_nomeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()));
		System.out.println(carrinhoPage.obter_tamanhoProduto());
		System.out.println(carrinhoPage.obter_corProduto());
		System.out.println(carrinhoPage.obter_inputQuantidadeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subTotalProduto()));
		
		System.out.println("*** ITENS TOTAIS ***");
		System.out.println(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subTotalTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxIncTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()));
		
		// Assercoes Hamcrest
		assertThat(carrinhoPage.obter_nomeProduto(), is(esperado_nomeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()), is(esperado_precoProduto));
		assertThat(carrinhoPage.obter_tamanhoProduto(), is(esperado_tamanhoProduto));
		assertThat(carrinhoPage.obter_corProduto(), is(esperado_corProduto));
		assertThat(Integer.parseInt(carrinhoPage.obter_inputQuantidadeProduto()), is(esperado_inputQuantidadeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subTotalProduto()), is(esperado_subtotalProduto));
		
		assertThat(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()), is(esperado_numeroItensTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subTotalTotal()), is(esperado_subTotalTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()), is(esperado_shippingTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()), is(esperado_totalTaxExclTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxIncTotal()), is(esperado_totalTaxIncTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()), is(esperado_taxesTotal));

		// Assercoes Junit
		assertEquals(esperado_nomeProduto, carrinhoPage.obter_nomeProduto());
		assertEquals(esperado_precoProduto, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()));
		assertEquals(esperado_tamanhoProduto, carrinhoPage.obter_tamanhoProduto());
		assertEquals(esperado_corProduto, carrinhoPage.obter_corProduto());
		assertEquals(esperado_inputQuantidadeProduto, Integer.parseInt(carrinhoPage.obter_inputQuantidadeProduto()));
		assertEquals(esperado_subtotalProduto, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subTotalProduto()));
		
		assertEquals(esperado_numeroItensTotal, Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()));
		assertEquals(esperado_subTotalTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subTotalTotal()));
		assertEquals(esperado_shippingTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()));
		assertEquals(esperado_totalTaxExclTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()));
		assertEquals(esperado_totalTaxIncTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxIncTotal()));
		assertEquals(esperado_taxesTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()));
	}
	
	CheckoutPage checkoutPage;
	
	@Test
	public void testIrParaCheckout_FreteMeioPagamentoEnderecoListadosOk() {
		// Pr�-condicao: Produto disponivel no carrinho de compras
		testIrParaCarrinho_InformacoesPersistidas();
		
		// Teste
		// Clicar no Botao
		checkoutPage = carrinhoPage.clicarBotaoProceedToCheckout();
		
		// Validar Informacoes na tela
		// Validando total Total (tax incl.)
		assertThat(Funcoes.removeCifraoDevolveDouble(checkoutPage.obter_totalTaxIncTotal()), is(esperado_totalTaxIncTotal));
		
		// Validando ADDRESSES
		assertTrue(checkoutPage.obter_nomeCliente().startsWith(esperado_nomeCliente));
		checkoutPage.clicarBotaoContinueAddress();
		
		// Validando SHIPPING METHOD
		String encontradoShippingValor = checkoutPage.obter_shippingValor();
		encontradoShippingValor = Funcoes.removeTexto(encontradoShippingValor, " tax excl.");
		Double encontradoShippingValorDouble = Funcoes.removeCifraoDevolveDouble(encontradoShippingValor);
		assertThat(encontradoShippingValorDouble, is(esperado_shippingTotal));
		checkoutPage.clicarBotaoContinueShipping();
		
		// Validando Payment
		// Selecionar opcao "Pay by Check"
		checkoutPage.selecionarRadioPayByCheck();
		
		// Validar valor do cheque
		String encontradoAmountPayByCheck = checkoutPage.obter_amountPayByCheck();
		encontradoAmountPayByCheck = Funcoes.removeTexto(encontradoAmountPayByCheck, " (tax incl.)");
		Double encontradoAmountPayByCheckDouble = Funcoes.removeCifraoDevolveDouble(encontradoAmountPayByCheck);
		assertThat(encontradoAmountPayByCheckDouble, is(esperado_totalTaxIncTotal));
		
		//Clicar op��o "I agree"
		checkoutPage.selecionarCheckBoxIAgree();
		assertTrue(checkoutPage.estaSelecionadoCheckBoxIAgree());
	}
	
	@Test
	public void testFinalizarPedido_pedidoFinalizadoComSucesso() {
		// Pre-condicoes: Checkout completamente concluido
		testIrParaCheckout_FreteMeioPagamentoEnderecoListadosOk();
		
		// Teste
		// Clicar no botao confirmar pedido
		PedidoPage pedidoPage = checkoutPage.clicarBotaoConfirmaPedido();
		
		// Validar valores da tela
		assertTrue(pedidoPage.obter_textoPedidoConfirmado().endsWith("YOUR ORDER IS CONFIRMED"));
		  //assertThat(pedidoPage.obter_textoPedidoConfirmado().toUpperCase(), is("YOUR ORDER IS CONFIRMED"));
		assertThat(pedidoPage.obter_email(), is("marcelo@teste.com"));
		assertThat(pedidoPage.obter_totalProdutos(), is(esperado_subtotalProduto));
		assertThat(pedidoPage.obter_totalTaxIncl(), is(esperado_totalTaxIncTotal));
		assertThat(pedidoPage.obter_metodoPagamento(), is("check"));
	}
	
	
}
