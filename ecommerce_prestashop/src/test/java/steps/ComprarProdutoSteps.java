package steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.common.io.Files;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import pages.HomePage;
import pages.LoginPage;
import pages.ModalProdutoPage;
import pages.ProdutoPage;

public class ComprarProdutoSteps {

	private static WebDriver driver;
	private HomePage homePage = new HomePage(driver);

	@Before
	public static void inicializar() {
		System.setProperty("webdriver.chrome.driver", "C:\\webdrivers\\chromedriver\\96\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	@Dado("que estou na pagina inicial")
	public void que_estou_na_pagina_inicial() {
		homePage.carregarPaginaInicial();
		assertThat(homePage.obterTituloPagina(), is("Loja de Teste"));
	}

	@Quando("nao estou logado")
	public void nao_estou_logado() {
		assertThat(homePage.estaLogado(), is(false));
	}

	@Entao("visualizo {int} produtos disponiveis")
	public void visualizo_produtos_disponiveis(Integer quantidadeProdutos) {
		assertThat(homePage.contarProdutos(), is(quantidadeProdutos));
	}

	@Entao("carrinho esta zerado")
	public void carrinho_esta_zerado() {
		assertThat(homePage.obterQuantidadeProdutosNoCarrinho(), is(0));
	}

	LoginPage loginPage;

	@Quando("estou logado")
	public void estou_logado() {
		// Click no botao Sign In na home Page
		loginPage = homePage.clicarBotaoSignIn();

		// Preencher Usuario e Senha
		loginPage.preencherEmail("marcelo@teste.com");
		loginPage.preencherPassword("marcelo");

		// Click no Botao Sign In para Logar
		loginPage.clicarBotaoSignIn();

		// Validar se o usuario esta logado de fato
		assertThat(homePage.estaLogado("Marcelo Bittencourt"), is(true));

		homePage.carregarPaginaInicial();
	}

	ProdutoPage produtoPage;
	String nomeProdutoHomePage;
	String precoProdutoHomePage;
	String nomeProdutoProdutoPage;
	String precoProdutoProdutoPage;

	@Quando("seleciono um produto na posicao {int}")
	public void seleciono_um_produto_na_posicao(Integer indice) {
		nomeProdutoHomePage = homePage.obterNomeProduto(indice);
		precoProdutoHomePage = homePage.obterPrecoProduto(indice);

		System.out.println(nomeProdutoHomePage);
		System.out.println(precoProdutoHomePage);

		produtoPage = homePage.clicarProduto(indice);

		nomeProdutoProdutoPage = produtoPage.obterNomeProduto();
		precoProdutoProdutoPage = produtoPage.obterPrecoProduto();
	}

	@Quando("nome do produto na tela principal e na tela produto eh {string}")
	public void nome_do_produto_na_tela_principal_eh(String nomeProduto) {
		assertThat(nomeProdutoHomePage.toUpperCase(), is(nomeProduto.toUpperCase()));
		assertThat(nomeProdutoProdutoPage.toUpperCase(), is(nomeProduto.toUpperCase()));
	}

	@Quando("preco do produto na tela principal e na tela produto eh {string}")
	public void preco_do_produto_na_tela_principal_eh(String precoProduto) {
		assertThat(precoProdutoHomePage, is(precoProduto.toUpperCase()));
		assertThat(precoProdutoProdutoPage, is(precoProduto.toUpperCase()));
	}

	ModalProdutoPage modalProdutoPage;

	@Quando("adiciono o produto no carrinho com tamanho {string} cor {string} e quantidade {int}")
	public void adiciono_o_produto_no_carrinho_com_tamanho_cor_e_quantidade(String tamanhoProduto, String corProduto,
			Integer quantidadeProduto) {

		// Selecionar tamanho
		List<String> listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		System.out.println(listaOpcoes);

		produtoPage.selecionarOpcaoDropDown(tamanhoProduto);

		listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		System.out.println(listaOpcoes);

		// Selecionar cor
		if (!corProduto.equals("N/A"))
			produtoPage.selecionarCorPreta();

		// Selecionar Quantidade
		produtoPage.alterarQuantidade(quantidadeProduto);

		// Adicionar ao carrinho
		modalProdutoPage = produtoPage.clicarBotaoAddToCart();

		// Validacoes
		assertTrue(modalProdutoPage.obterMensagemProdutoAdicionado()
				.endsWith("Product successfully added to your shopping cart"));
	}

	@Entao("o produto aparece na confirmacao com nome {string} preco {string} tamanho {string} cor {string} e quantidade {int}")
	public void o_produto_aparece_na_confirmacao_com_nome_preco_tamanho_cor_e_quantidade(String nomeProduto,
			String precoProduto, String tamanhoProduto, String corProduto, Integer quantidadeProduto) {

		assertThat(modalProdutoPage.obterDescricaoProduto().toUpperCase(), is(nomeProdutoProdutoPage.toUpperCase()));

		Double precoProdutoDoubleEncontrado = Double.parseDouble(modalProdutoPage.obterPrecoProduto().replace("$", ""));
		Double precoProdutoDoubleEsperado = Double.parseDouble(precoProduto.replace("$", ""));

		assertThat(modalProdutoPage.obterTamanhoProduto(), is(tamanhoProduto));
		if (!corProduto.equals("N/A"))
			assertThat(modalProdutoPage.obterCorProduto(), is(corProduto));
		assertThat(modalProdutoPage.obterQuantidadeProduto(), is(Integer.toString(quantidadeProduto)));

		String subTotalString = modalProdutoPage.obterSubtotal();
		subTotalString = subTotalString.replace("$", "");
		Double subTotalEncontrado = Double.parseDouble(subTotalString);

		Double subTotalCalculadoEsperado = quantidadeProduto * precoProdutoDoubleEsperado;
		assertThat(subTotalEncontrado, is(subTotalCalculadoEsperado));
	}

	@After(order = 1)
	public void capturarTela(Scenario scenario) {
		TakesScreenshot camera = (TakesScreenshot) driver;
		File capturaDeTela = camera.getScreenshotAs(OutputType.FILE);
		
		String scenarioId = scenario.getId().substring(scenario.getId().lastIndexOf(".feature:") + 9);
		
		String nomeArquivo = "resources/screenshots/" + scenario.getName() + "_" + scenarioId + "_" + scenario.getStatus() + ".png";
		System.out.println(nomeArquivo);

		try {
			Files.move(capturaDeTela, new File(nomeArquivo));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@After(order = 0)
	public static void finalizar() {
		driver.quit();
	}

}
