// ktlint-disable max-line-length

package io.element.android.features.zerorewards.impl.faqs

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

private val HYPER_LINK_COLOR = Color(0xFF01F4CB)

object RewardScreenFAQs {
	private val faqs =
		listOf(
			RewardFAQ(
				question = "How do I earn daily income?",
				answer =
				buildAnnotatedString {
					val str =
						"""
ZERO Messenger rewards all active users by distributing rewards from a daily pool.
The criteria that determine daily ZBI payments include:

1. Messaging - Having conversations in the app contributes to your daily allotment.
2. Invites - Inviting friends who sign up and join the app gives you a ZBI bump.
3. Refer-a-Mint - Inviting friends who join Messenger and then go on to mint a ZERO ID will earn YOU rewards too!
4. Friends Inviting Friends - Invitees of friends you've invited also help you earn.
5. Refer-a-Friend-of-a-Friend-to-Mint - Friends of friends minting Worlds or Domains in the ZERO ID Explorer app earns you some trickle-up rewardonomics!
						""".trimIndent()
					val mintAZeroId = "mint a ZERO ID"
					val mintAZeroIdStart = str.indexOf(mintAZeroId)
					val mintAZeroIdEnd = mintAZeroIdStart.plus(mintAZeroId.length)

					val zeroIdExplorer = "ZERO ID Explorer"
					val zeroIdExplorerStart = str.indexOf(zeroIdExplorer)
					val zeroIdExplorerEnd = zeroIdExplorerStart.plus(zeroIdExplorer.length)

					val linkColor = HYPER_LINK_COLOR

					addStyle(
						style =
						SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
						start = mintAZeroIdStart,
						end = mintAZeroIdEnd
					)
					addStyle(
						style =
						SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
						start = zeroIdExplorerStart,
						end = zeroIdExplorerEnd
					)

					addStringAnnotation(
						tag = mintAZeroId,
						annotation = "https://explorer.zero.tech/",
						start = mintAZeroIdStart,
						end = mintAZeroIdEnd
					)
					addStringAnnotation(
						tag = zeroIdExplorer,
						annotation = "https://explorer.zero.tech/",
						start = zeroIdExplorerStart,
						end = zeroIdExplorerEnd
					)

					append(str)
				}
			),
			RewardFAQ(
				question = "This seems too good to be true?",
				answer =
				buildAnnotatedString {
					val str =
						"""
						At ZERO, we believe a product cannot exist without the people who use it. By disbursing MEOW to our Messenger community, we're distributing the value of ZERO to those that bring it to life — you. Of course, we can't do this forever; early users of ZERO Messenger will be rewarded more than latecomers. As we scale into the future, individual payouts will diminish and we'll transition to a new model.
						""".trimIndent()

					append(str)
				}
			),
			RewardFAQ(
				question = "What is MEOW?",
				answer =
				buildAnnotatedString {
					val str =
						"""
MEOW (ticker symbol $ MEOW) is an ERC-20 standard cryptocurrency token on the Ethereum blockchain. It is the native currency of the ZERO ecosystem, powering a suite of native zApps, including our unique identity solution — ZERO ID — and our ZERO blockchain browser, Explorer. MEOW can be swapped for Ethereum or tradition fiat-backed currencies like USDC at major DeFi exchanges, like Uniswap.

MEOW is a used by a wider ecosystem of projects. Read more Here.
						""".trimIndent()
					val explorer = "Explorer"
					val explorerStart = str.indexOf(explorer)
					val explorerEnd = explorerStart.plus(explorer.length)

					val uniswap = "Uniswap"
					val uniswapStart = str.indexOf(uniswap)
					val uniswapEnd = uniswapStart.plus(uniswap.length)

					val here = "Here"
					val hereStart = str.indexOf(here)
					val hereEnd = hereStart.plus(here.length)

					val linkColor = HYPER_LINK_COLOR

					addStyle(
						style =
						SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
						start = explorerStart,
						end = explorerEnd
					)
					addStyle(
						style =
						SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
						start = uniswapStart,
						end = uniswapEnd
					)

					addStyle(
						style =
						SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline),
						start = hereStart,
						end = hereEnd
					)

					addStringAnnotation(
						tag = explorer,
						annotation = "https://explorer.zero.tech/",
						start = explorerStart,
						end = explorerEnd
					)
					addStringAnnotation(
						tag = uniswap,
						annotation =
						"https://app.uniswap.org/swap?outputCurrency=0x0eC78ED49C2D27b315D462d43B5BAB94d2C79bf8&inputCurrency=ETH&use=V2",
						start = uniswapStart,
						end = uniswapEnd
					)

					addStringAnnotation(
						tag = here,
						annotation = "https://www.meow.inc/",
						start = hereStart,
						end = hereEnd
					)

					append(str)
				}
			),
			RewardFAQ(
				question = "What is ZERO ID?",
				answer =
				buildAnnotatedString {
					append(
						"""
ZERO ID is the native identity management system powering the ZERO ecosystem. Everything in Messenger is tied to your ZERO ID; it is your digital passport and your key to unlocking the full potential of ZBI! ZERO ID comprises two type of domains, represented as ERC-721 NFTs on the Ethereum blockchain: Worlds and Domains. Worlds are the top-level domain in the system (0://hello) and are ideally suited for communities and organizations. Domains are second-level-and-beyond subdomains in the system, existing under Worlds (0://hello.goodbye), but also having the ability to mint domains under themselves (0://hello.goodbye.bonjour, 0://hello.goodbye.bonjour.adieu, and so on)!
						""".trimIndent()
					)
				}
			),
			RewardFAQ(
				question = "How can I withdraw my MEOW?",
				answer =
				buildAnnotatedString {
					append(
						"The ability to withdraw your earned MEOW to an external wallet will be added soon!".trimIndent()
					)
				}
			)
		)

	fun getFAQs() = faqs
}

data class RewardFAQ(val question: String = "", val answer: AnnotatedString)
