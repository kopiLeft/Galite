##
## Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
##
## This library is free software; you can redistribute it and/or
## modify it under the terms of the GNU Lesser General Public
## License version 2.1 as published by the Free Software Foundation.
##
## This library is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
## Lesser General Public License for more details.
##
## You should have received a copy of the GNU Lesser General Public
## License along with this library; if not, write to the Free Software
## Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
##
## $Id: Makefile 35325 2018-08-29 11:27:17Z iayadi $
##

TOPDIR =		../../..

NONSTANDARDBUILD =	true

TARGETDIR =		$(CLASSROOT)/org/kopi/vkopi/lib/resource

## ----------------------------------------------------------------------
## TOOLS

## ----------------------------------------------------------------------
## FILES

FREEGIFS =

OLDGIFS =	all apply arrowleft arrowright article			\
		ask bkup bkup3 block block2 board bomb			\
		bookmark boxarrow break bw calculate			\
		cfolder clip collapsed collapsed_f			\
		collapsed_s collapsed_t collapsedb combo		\
		config convert copy delete deleteline			\
		desk detail done duke edit		\
		error expanded expanded_f expanded_s			\
		expanded_t expandedb export fax fold			\
		foldColumn form form_f form_s form_t			\
		forms forms_f forms_s forms_t fw gifIcon		\
		green guide help home index info info2			\
		insert insertline interrupt jpgIcon			\
		launch list lock login mail menuquery			\
		money moneycheck noIcon note nothing			\
		notice ofolder open options pageLeft			\
		pageRight phone preview print				\
		printoptions project quit red refresh			\
		reload report save search				\
		search2 searchop sec selected send			\
		serialquery serviceoff serviceon sort			\
		split standard stick stop store suggest			\
		todo top unfold unfoldColumn unstick			\
		users utils validate wait warning window		\
		yellow arrowfirst arrowlast


PNGFILES =	add all apply  article					\
		ask bkup bkup3 block block2 board bomb			\
		bookmark boxarrow break bw clip				\
		config convert copy delete deleteline			\
		desk detail done duke edit				\
		error  export fold					\
		foldColumn detail_view chart_view			\
		 fw gifIcon						\
		help home calculate					\
		insert insertline  jpgIcon				\
		launch list lock login mail menuquery			\
		money  note nothing					\
		notice  open options pageLeft				\
		pageRight pageFirst pageLast phone preview print	\
		printoptions project quit  refresh			\
		reload report save search				\
		search2 searchop sec  send				\
		serialquery serviceoff serviceon sort			\
		split standard  stop store suggest			\
		todo top unfold unfoldColumn update			\
		users utils validate wait warning forms checkbox \
		checked unchecked default checked_s unchecked_s   \
		default_s radio_checked radio_checked_s

PNGNEW =	align_center align_justify align_left align_right bold	\
		cut ident italic paste redo unident underline undo      \
		border password zoomminus zoomwidth zoomheight		\
		zoomoptimal zoomplus formula area_chart column_chart	\
		pie_chart bar_chart line_chart

GIFFILES =	$(FREEGIFS) $(OLDGIFS)

PSFILES =	StoneGerman german report

LOCALIZATIONS =	General HelpViewer Preview Menu Window Messages		\
		VKMessages VlibProperties RootMenu

OTHERFILES =	general.tex macros.tex splash.jpg			\
		titlepage.sty top.tex					\


FILES =		$(PNGFILES:%=%.png)					\
		$(PNGNEW:%=%.png)					\
		$(GIFFILES:%=%.gif)					\
		$(PSFILES:%=%.ps)					\
		$(LOCALIZATIONS:%=%-de_AT.xml)				\
		$(LOCALIZATIONS:%=%-fr_FR.xml)				\
		$(LOCALIZATIONS:%=%-en_GB.xml)				\
		$(LOCALIZATIONS:%=%-ar_TN.xml)				\
		$(OTHERFILES)

## ----------------------------------------------------------------------
## GOALS

default:	build

build:		$(TARGETDIR) $(FILES:%=$(TARGETDIR)/%)


$(TARGETDIR):
		mkdir $(TARGETDIR)

$(TARGETDIR)/%:	%
		cp $< $(TARGETDIR)

## ----------------------------------------------------------------------
## SPECIAL TARGETS

gallery.html:	$(GIFFILES:%=%.gif)
		sh makegallery.sh $^ > $@

## ----------------------------------------------------------------------
## GLOBAL DEFINITIONS

include $(TOPDIR)/Make.Defs
