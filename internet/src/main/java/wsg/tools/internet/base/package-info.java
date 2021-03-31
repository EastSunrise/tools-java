/**
 * This package provides fundamental interfaces and their skeletal implementations to retrieve data
 * or information from the Internet.
 * <p>
 * A website from the Internet, known as a collection of webpages, is defined as a
 * <strong>site</strong> which is annotated by {@code ConcreteSite}. The data retrieved from the
 * webpages of a website are grouped according to certain rules. Each group of data is treat as a
 * <strong>repository</strong> which usually contains functions to retrieve and handle data.
 *
 * @author Kingen
 * @see wsg.tools.internet.base.ConcreteSite
 * @see wsg.tools.internet.base.repository.Repository
 * @since 2021/3/4
 */
package wsg.tools.internet.base;