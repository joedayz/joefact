# ************************************************************
# Sequel Pro SQL dump
# Version 5224
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.7.22)
# Database: facturaelectronica
# Generation Time: 2018-09-10 06:56:05 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table anticipos
# ------------------------------------------------------------

DROP TABLE IF EXISTS `anticipos`;

CREATE TABLE `anticipos` (
  `idanticipos` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Codigo de tabla Autogenerado',
  `idExterno` varchar(50) NOT NULL COMMENT 'Id de Control de documento externo',
  `docu_codigo` int(11) NOT NULL COMMENT 'Codigo relacion de la tabla cabecera',
  `docu_anticipo_prepago` varchar(45) DEFAULT NULL COMMENT 'Monto prepagado o anticipado',
  `docu_anticipo_docu_tipo` varchar(45) DEFAULT NULL COMMENT 'Documento del anticipo - Tipo de documento - CatÃ¡logo No. 12 ( 02  factura anticipo / 03 Boleta Venta - anticipo)',
  `docu_anticipo_docu_numero` varchar(45) DEFAULT NULL COMMENT 'Documento del anticipo - (Serie - NÃºmero de documento)',
  `docu_anticipo_tipo_docu_emi` varchar(45) DEFAULT NULL COMMENT 'Ruc del emisor del documento de anticipo - Tipo  6',
  `docu_anticipo_numero_docu_emi` varchar(45) DEFAULT NULL COMMENT 'Ruc del emisor del documento de anticipo - numero',
  `id_externo` varchar(255) NOT NULL,
  PRIMARY KEY (`idanticipos`),
  KEY `fk_anticipos_cabecera1_idx` (`docu_codigo`),
  CONSTRAINT `fk_anticipos_cabecera1` FOREIGN KEY (`docu_codigo`) REFERENCES `cabecera` (`docu_codigo`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Tabla de realacion de documentos de acticipos que afecta a una Boleta o Factura final\n';



# Dump of table cabecera
# ------------------------------------------------------------

DROP TABLE IF EXISTS `cabecera`;

CREATE TABLE `cabecera` (
  `docu_codigo` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Codigo de tabla Autogenerado',
  `idExterno` varchar(50) DEFAULT NULL COMMENT 'Codigo externo, para control de ERPs.',
  `empr_razonsocial` varchar(200) DEFAULT NULL COMMENT 'Razon social de la empresa',
  `empr_ubigeo` varchar(6) DEFAULT NULL COMMENT 'Codigo de Ubigeo de la empresa',
  `empr_nombrecomercial` varchar(200) DEFAULT NULL COMMENT 'Nombre Comercial de la empresa',
  `empr_direccion` varchar(200) DEFAULT NULL COMMENT 'Direccion de la Empresa',
  `empr_provincia` varchar(200) DEFAULT NULL COMMENT 'Provincia de la empresa',
  `empr_departamento` varchar(200) DEFAULT NULL COMMENT 'Departamento de la empresa',
  `empr_distrito` varchar(200) DEFAULT NULL COMMENT 'Distrito de la empresa',
  `empr_pais` varchar(200) DEFAULT NULL COMMENT 'Pais de la empresa - por defecto PE',
  `empr_nroruc` varchar(11) DEFAULT NULL COMMENT 'Numero de Ruc de la empresa',
  `empr_tipodoc` varchar(45) DEFAULT NULL COMMENT 'Tiop de documento de la empresa - por defecto  6 - RUC, segun Catalogo 1 del anexo 8\n',
  `clie_numero` varchar(45) DEFAULT NULL COMMENT 'Numero del Documento del cliente RUC/ DNI/Otros.',
  `clie_tipodoc` varchar(45) DEFAULT NULL COMMENT 'Tipo de documento del cliente  - 0 Sin Documento, 1  DNI, 6  - RUC, segun Catalogo 1 del anexo 8',
  `clie_nombre` varchar(200) DEFAULT NULL COMMENT 'Nombre / Razon Social del cliente',
  `docu_fecha` varchar(45) DEFAULT NULL COMMENT 'fecha del documento formato YYYY-MM-DD, ej. 2017-06-21',
  `docu_tipodocumento` varchar(45) DEFAULT NULL COMMENT 'Tipo de Documento \n01 - Factura\n03 - Boleta de Venta\n07 - Nota de Credito\n08 - Nota de Debito\n20 - Retenciones',
  `docu_numero` varchar(45) DEFAULT NULL COMMENT 'Numero de Documento\nformat \nserie - numero\nserie: \nF000 factura y sus Notas\nB000 Boleta  y sus Notas\nR000 Retenciones\n-------------\nNota de credito  y Nota de Debito\nsi e a una factura\nF000\nsi es a una Boleta\nB000',
  `docu_moneda` varchar(45) DEFAULT NULL COMMENT 'Moneda del documento, \nPEN - Soles\nUSD - Dollar',
  `docu_gravada` varchar(45) DEFAULT NULL COMMENT 'Importe total de Documento grabado al IGV',
  `docu_inafecta` varchar(45) DEFAULT NULL COMMENT 'importe total de  inafecto del documento',
  `docu_exonerada` varchar(45) DEFAULT NULL COMMENT 'importe total exonerado del IGV  del documento',
  `docu_gratuita` varchar(45) DEFAULT NULL COMMENT 'importe total de valores gratuitos  del documento',
  `docu_descuento` varchar(45) DEFAULT NULL COMMENT 'importe total de descuento del documento',
  `docu_subtotal` varchar(45) DEFAULT NULL COMMENT 'importe subtotal del documento',
  `docu_total` varchar(45) DEFAULT NULL COMMENT 'importe total  del documento\n importe total retenido+ igv + inafectos + exonerados + otros cargos + isc + otros tributos.\n\nEn caso de Retencion, el valor de ls retencion (3% del valor totales de los documentos) ',
  `docu_igv` varchar(45) DEFAULT NULL COMMENT 'Importe totales del IGV del documento',
  `tasa_igv` varchar(45) DEFAULT NULL COMMENT 'Tasa del IGV =18 ',
  `docu_isc` varchar(45) DEFAULT NULL COMMENT 'Importe  del ISC',
  `tasa_isc` varchar(45) DEFAULT NULL COMMENT 'Tasa ISC',
  `docu_otrostributos` varchar(45) DEFAULT NULL COMMENT 'Otros importes de otros tributos',
  `tasa_otrostributos` varchar(45) DEFAULT NULL COMMENT 'Tasa de otros tributos',
  `rete_regi` varchar(45) DEFAULT NULL COMMENT 'Tipo de regimem de retencion por defecto 01 (3%)',
  `rete_tasa` varchar(45) DEFAULT NULL COMMENT 'Tasa de retencion 3',
  `rete_total_elec` varchar(45) DEFAULT NULL COMMENT 'Total de importe de retencion luego del descuento de la retencion.(rete_total_rete-docu_total)',
  `rete_total_rete` varchar(45) DEFAULT NULL COMMENT 'Totales de los documentos que se afectan a esta Retencion',
  `docu_otroscargos` varchar(45) DEFAULT NULL COMMENT 'Otros cargos que afectan al Documento',
  `docu_percepcion` varchar(45) DEFAULT NULL COMMENT 'Importe de percepcion',
  `nota_motivo` varchar(45) DEFAULT NULL COMMENT 'Codigo del motivo de la nota de credito catalogo 9 / Debito catalogo 10 (anexo 8)\n',
  `nota_sustento` varchar(250) DEFAULT NULL COMMENT 'Sustento de la nota de credito',
  `nota_tipodoc` varchar(45) DEFAULT NULL COMMENT 'Tipo de documento que modifica o afecta la Nota de Credito o Debito.\n01- Factura\n03 - Boleta\n',
  `nota_documento` varchar(45) DEFAULT NULL COMMENT 'Numero del Documento relacionado que afecta la Nota de Credito o Debito.\nF00-0001\nB00-0001',
  `hashcode` varchar(100) DEFAULT NULL COMMENT 'Has code de la impresion\n\nsYlI7o6ndTiOEYz+Yv4jmz3CnWY=',
  `barcode` blob COMMENT 'Bar code estandard PDF417/QR',
  `cdr` varchar(200) DEFAULT NULL COMMENT 'Estado del CDR',
  `cdr_nota` varchar(500) DEFAULT NULL COMMENT 'Nota del CDR',
  `cdr_observacion` varchar(2000) DEFAULT ' ' COMMENT 'Notas de Observaciones del CDR',
  `docu_enviaws` varchar(45) DEFAULT 'S' COMMENT 'Estado si el documento se envia al ws de SUNAT\nS- Si se envia.\nN- No se envia.',
  `docu_proce_status` varchar(45) DEFAULT 'N' COMMENT '* - Insertando a tablas cabecera y Detalle.\nN - Nuevo\nB - Bloqueo\nP - Proceso-\nE - Enviado\nX - Error de Envio\n\n',
  `docu_proce_fecha` datetime DEFAULT NULL COMMENT 'Fecha del proceso de la generacion del Documento Electronico.',
  `docu_link_pdf` varchar(200) DEFAULT NULL COMMENT 'Ruta del archivo pdf',
  `docu_link_cdr` varchar(200) DEFAULT NULL COMMENT 'Ruta del archivo cdr',
  `docu_link_xml` varchar(200) DEFAULT NULL COMMENT 'Ruta del archivo Xml',
  `docu_forma_pago` varchar(50) DEFAULT NULL COMMENT 'Forma de Pago',
  `docu_observacion` varchar(250) DEFAULT NULL COMMENT 'Observacion del documento',
  `clie_direccion` varchar(200) DEFAULT NULL COMMENT 'Direccion del Cliente',
  `docu_vendedor` varchar(100) DEFAULT NULL COMMENT 'Nombre del Vendedor',
  `docu_pedido` varchar(50) DEFAULT NULL COMMENT 'Nro de Pedido ',
  `docu_guia_remision` varchar(20) DEFAULT NULL COMMENT 'Guia de Remision',
  `clie_orden_compra` varchar(30) DEFAULT NULL COMMENT 'Orden de compra cliente',
  `clie_correo_cpe1` varchar(100) DEFAULT NULL COMMENT 'Correo cliente 1',
  `clie_correo_cpe2` varchar(100) DEFAULT NULL COMMENT 'Correo cliente 2',
  `clie_correo_cpe0` varchar(100) DEFAULT NULL COMMENT 'Correo cliente 0 - contacto principal',
  `docu_tipo_operacion` varchar(5) DEFAULT '-' COMMENT 'Opcional - Tipo de operaciÃ³n  - Anexo 8 Catalogo 17 Tipo de operaciones (04 venta interna anticipo)\n',
  `docu_anticipo_total` varchar(45) DEFAULT '0.00' COMMENT 'Total Anticipos - suma de los detalles de anticipos ( solo si hay pgos acticipados como parte del detalle)',
  `empr_direccion_suc` varchar(200) DEFAULT NULL COMMENT 'Direccion de la sucursal',
  `empr_ubigeo_suc` varchar(6) DEFAULT NULL COMMENT 'Ubigeo de sucursal',
  `empr_departamento_suc` varchar(200) DEFAULT NULL COMMENT 'Departamento sucursal',
  `empr_provincia_suc` varchar(200) DEFAULT NULL COMMENT 'Provincia sucursal',
  `empr_distrito_suc` varchar(200) DEFAULT NULL COMMENT 'Distrito sucursal\n',
  `resu_fecha_generacion` varchar(45) DEFAULT NULL COMMENT 'fecha de la comunicacion o envio ',
  `resu_identificador` varchar(45) DEFAULT NULL COMMENT 'Indentificador para el envio a SUNAT se genera con la fecha del envio - cabecera de resumen de boletas y sus notas',
  `resu_fila` varchar(45) DEFAULT NULL COMMENT 'Numero de orden del resumen',
  `resu_version` varchar(5) DEFAULT NULL COMMENT 'Version del proceso  y envio de resumeen de Boletas y sus notas\n1.0 hasta 20161231\n1.1 desde 20170101',
  `resu_estado` varchar(45) DEFAULT NULL COMMENT 'Estado del Resumen de Diarios\nP - Preparado\nR - Reportado a sunat\n',
  `item_estado` varchar(45) DEFAULT '1' COMMENT 'Estado del registros\n1- Adiciona\n2.- Modifica\n3.-Anulado de otro dia\n4.-Anulado del mismo dia',
  `flg_xml` varchar(1) DEFAULT '1' COMMENT 'flg de control para generar xml',
  `flg_pdf` varchar(1) DEFAULT '1' COMMENT 'flg de control para generar pdf',
  `flg_ftp` varchar(1) DEFAULT '1' COMMENT 'flg de control para enviar a un ftp',
  `flg_sunat` varchar(1) DEFAULT '1' COMMENT 'flg de control para enviar a sunat',
  `flg_email` varchar(1) DEFAULT '1' COMMENT 'flg de control para enviar email',
  `resu_fecha_generacion_baja` varchar(10) DEFAULT NULL COMMENT 'fecha de generacion para resumenes solo bajas\n',
  `resu_identificador_baja` varchar(45) DEFAULT NULL COMMENT 'Indentificador para el envio a SUNAT se genera con la fecha del envio - cabecera de resumen de boletas y sus notas, este es silo para bajas',
  `resu_fila_baja` varchar(3) DEFAULT NULL COMMENT 'Orden del resumen diaro, solo para bajas',
  `item_estado_baja` varchar(1) DEFAULT NULL COMMENT 'Estado del registros\n3.-Anulado de otro dia',
  `id_externo` varchar(255) DEFAULT NULL,
  `actualizador` varchar(255) DEFAULT NULL,
  `creador` varchar(255) DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT NULL,
  `row_version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`docu_codigo`),
  UNIQUE KEY `idExterno_UNIQUE` (`idExterno`),
  KEY `idExterno_Cab_idx` (`idExterno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Tabla maestro de Documentos electronicos - Facturas, Boletas Notas de Credito y Debito, Retenciones\n';



# Dump of table cabecera_guia_factura
# ------------------------------------------------------------

DROP TABLE IF EXISTS `cabecera_guia_factura`;

CREATE TABLE `cabecera_guia_factura` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `docu_codigo` int(11) NOT NULL,
  `tipo_docu` varchar(2) DEFAULT NULL COMMENT 'Tipo documento 01 Factura o 09 Guia de Remision',
  `numero_doc` varchar(13) DEFAULT NULL COMMENT 'Numero de documento',
  `id_emisor` varchar(11) DEFAULT NULL COMMENT 'Documento de emisor',
  `nombre_social_ermisor` varchar(100) DEFAULT NULL,
  `destinatario_tipo_doc` varchar(2) DEFAULT NULL,
  `destinatario_nro_doc` varchar(15) DEFAULT NULL,
  `destinatario_nombre_social` varchar(100) DEFAULT NULL,
  `sujeto_traslado_c` varchar(2) DEFAULT NULL COMMENT 'No obligsatorio\nPuede tomar el valor de: \n01 - Emisor ElectrÃ³nico â€“ Vendedor (Num. 3.1.1 Art. 21 RCP)\n02 - Comprador â€“ Adquiriente o poseedor del bien (Num. 1.1 Art. 21 RCP)',
  `peso_bruto_total_peso_c` varchar(16) DEFAULT NULL COMMENT 'Peso del carga n(12,3)',
  `peso_bruto_total_tipo_c` varchar(4) DEFAULT NULL COMMENT 'CatÃ¡logo NÂ° 03\n=KG\n',
  `peso_bruto_total_kg_c` varchar(4) DEFAULT NULL,
  `modalidad_transporte_c` varchar(2) DEFAULT NULL COMMENT 'CatÃ¡logo NÂ° 18\n',
  `fecha_traslado_inicio` varchar(10) DEFAULT NULL COMMENT 'YYYY-MM-DD',
  `transportista_tipo_doc_c` varchar(2) DEFAULT NULL,
  `transportista_numero_c` varchar(11) DEFAULT NULL,
  `transportista_nombre_social_c` varchar(100) DEFAULT NULL,
  `transportista_registro_mtc_c` varchar(15) DEFAULT NULL,
  `vehiculo_principal_placa_c` varchar(8) DEFAULT NULL,
  `vehiculo_principal_secundario_c` varchar(8) DEFAULT NULL,
  `direccion_salida_ubigeo` varchar(8) DEFAULT NULL,
  `direccion_salida_direccion` varchar(100) DEFAULT NULL,
  `direccion_llegada_ubigeo` varchar(8) DEFAULT NULL,
  `direccion_llegada_direccion` varchar(100) DEFAULT NULL,
  `indicador_subcontrato_c` varchar(1) DEFAULT NULL COMMENT 'Indicador de subcontrataciÃ³n (en caso de Factura Guia Transportista)\ntrue/false',
  PRIMARY KEY (`id`),
  KEY `fk_cabecera_guia_factura_cabecera1_idx` (`docu_codigo`),
  CONSTRAINT `fk_cabecera_guia_factura_cabecera1` FOREIGN KEY (`docu_codigo`) REFERENCES `cabecera` (`docu_codigo`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table comunicabaja
# ------------------------------------------------------------

DROP TABLE IF EXISTS `comunicabaja`;

CREATE TABLE `comunicabaja` (
  `idcomunicabaja` int(11) NOT NULL AUTO_INCREMENT COMMENT 'PK de tabla',
  `codigo` int(11) NOT NULL COMMENT 'pk de tabls cabecera',
  `resu_fecha` varchar(45) DEFAULT NULL COMMENT 'Fecha de la generacion de la reversion del documento',
  `resu_fila` varchar(45) DEFAULT NULL COMMENT 'Numero correlativo del grupo o fila del grupo',
  `resu_tipodoc` varchar(45) DEFAULT NULL COMMENT 'Tipo de Documento segun catalogo 01: codigo de tipo de documento',
  `resu_serie` varchar(45) DEFAULT NULL COMMENT 'Numero de serie del documento electronico',
  `resu_numero` varchar(45) DEFAULT NULL COMMENT 'Numero correlativo del documento 1 hasta 8 digitos',
  `resu_motivo` varchar(45) DEFAULT NULL COMMENT 'Motivo por lo que se da de baja.\nEJ. \n1.- Error en generacion, \n2.- ANULADO.\n',
  `resu_identificador` varchar(45) DEFAULT NULL COMMENT 'Identificador de cabecera',
  PRIMARY KEY (`idcomunicabaja`),
  KEY `fk_comunicabaja_resumendia_baja1_idx` (`codigo`),
  CONSTRAINT `fk_comunicabaja_resumendia_baja1` FOREIGN KEY (`codigo`) REFERENCES `resumendia_baja` (`codigo`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Detalle de los documentos de bajas ';



# Dump of table conductores
# ------------------------------------------------------------

DROP TABLE IF EXISTS `conductores`;

CREATE TABLE `conductores` (
  `idconductores` int(11) NOT NULL AUTO_INCREMENT,
  `guia_factura_id` int(11) NOT NULL,
  `tipo_doc` varchar(2) DEFAULT NULL COMMENT 'CatÃ¡logo NÂ° 06',
  `numero_doc` varchar(11) DEFAULT NULL COMMENT 'numero de documento segun tipo\n',
  PRIMARY KEY (`idconductores`),
  KEY `fk_conductores_cabecera_guia_factura1_idx` (`guia_factura_id`),
  CONSTRAINT `fk_conductores_cabecera_guia_factura1` FOREIGN KEY (`guia_factura_id`) REFERENCES `cabecera_guia_factura` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table detalle
# ------------------------------------------------------------

DROP TABLE IF EXISTS `detalle`;

CREATE TABLE `detalle` (
  `iddetalle` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Codigo autogenerado de la tabla',
  `idExterno` varchar(50) DEFAULT NULL,
  `docu_codigo` int(11) NOT NULL COMMENT 'Codigo relacion de la tabla cabecera',
  `docu_moneda` varchar(45) DEFAULT NULL COMMENT 'Moneda del Documento \nPEN - Soles\nUSD - Dollar',
  `item_moneda` varchar(45) DEFAULT NULL COMMENT 'Moneda del Item\nPEN - Soles\nUSD - Dollar',
  `item_orden` varchar(45) DEFAULT NULL COMMENT 'Numero de Orden',
  `item_unidad` varchar(45) DEFAULT NULL COMMENT 'Unidad de Medida del Item\ndefecto NIU para bienes y ZZ para servicios.',
  `item_cantidad` varchar(45) DEFAULT NULL COMMENT 'Cantidad de Item',
  `item_codproducto` varchar(45) DEFAULT NULL COMMENT 'Codigo del Producto como usa el ERP del emisor.',
  `item_descripcion` varchar(255) DEFAULT NULL COMMENT 'Descripcion del Producto maximo 250 caracteres.',
  `item_afectacion` varchar(45) DEFAULT NULL COMMENT 'Tipo de afectacion\n\n10 Gravado - OperaciÃ³n Onerosa (uso comun)\n\nverificar CATALOGO No. 07',
  `item_tipo_precio_venta` varchar(45) DEFAULT '01' COMMENT 'CÃ³digo de tipo de precio - CatÃ¡logo No. 16, por defecto ''01''  Precio unitario (incluye el IGV) \n',
  `item_pventa` varchar(45) DEFAULT NULL COMMENT 'Precio de venta unitario sin IGV',
  `item_pventa_nohonerosa` varchar(45) DEFAULT '0.00' COMMENT 'Valor referencial unitario por Ã­tem en operaciones no onerosas ( gratuito)',
  `item_ti_subtotal` varchar(45) DEFAULT NULL COMMENT 'Importe sub total',
  `item_ti_igv` varchar(45) DEFAULT NULL COMMENT 'Importe del IGV del item',
  `item_isc_tier` varchar(45) DEFAULT '01' COMMENT 'Tipos de Sistema de CÃ¡lculo del ISC\n\n01 - Sistema al valor (ApÃ©ndice IV, lit. A â€“ T.U.O IGV e ISC)\n\nverificar CATALOGO No. 08',
  `item_ti_isc` varchar(45) DEFAULT '0.00' COMMENT 'Importe del ISC del item',
  `rete_rela_tipo_docu` varchar(45) DEFAULT NULL COMMENT 'Retenciones Tipo de documento con retencion 01 factura',
  `rete_rela_nume_docu` varchar(45) DEFAULT NULL COMMENT 'Retenciones Numero de documento con retencion',
  `rete_rela_fech_docu` varchar(45) DEFAULT NULL COMMENT 'Retenciones fecha de retencion',
  `rete_rela_tipo_moneda` varchar(45) DEFAULT NULL COMMENT 'Retenciones Tipo de Moneda del documento con retencion\nPEN/USD',
  `rete_rela_total_original` varchar(45) DEFAULT NULL COMMENT 'Importe total del documento en moneda original',
  `rete_rela_fecha_pago` varchar(45) DEFAULT NULL COMMENT 'Fecha de pago de la retencion',
  `rete_rela_numero_pago` varchar(45) DEFAULT NULL COMMENT 'Numero de pago',
  `rete_rela_importe_pagado_original` varchar(45) DEFAULT NULL COMMENT 'Importe pagado del documento original\n',
  `rete_rela_tipo_moneda_pago` varchar(45) DEFAULT NULL COMMENT 'Tipo de moneda del documento de pago',
  `rete_importe_retenido_nacional` varchar(45) DEFAULT NULL COMMENT 'importe de retencion en moneda nacional',
  `rete_importe_neto_nacional` varchar(45) DEFAULT NULL COMMENT 'Importe Neto en moneda nacional',
  `rete_tipo_moneda_referencia` varchar(45) DEFAULT NULL COMMENT 'Tipo de moneda de la referencoa del documento retenido (USD)\n',
  `rete_tipo_moneda_objetivo` varchar(45) DEFAULT NULL COMMENT 'La moneda objetivo para la Tasa de\nCambio.\ndefecto PEN',
  `rete_tipo_moneda_tipo_cambio` varchar(45) DEFAULT NULL COMMENT 'El factor aplicado a la moneda de origen para calcular la moneda de destino (Tipo de cambio)',
  `rete_tipo_moneda_fecha` varchar(45) DEFAULT NULL COMMENT 'Fecha de cambio',
  `item_otros` varchar(5000) DEFAULT NULL COMMENT 'Otros datos del detalle, serie, modelo, etc etc ',
  `bultos` varchar(10) DEFAULT NULL COMMENT 'Bultos  Cofaco',
  `color` varchar(16) DEFAULT NULL COMMENT 'Color  Cofaco',
  `talla` varchar(2) DEFAULT NULL COMMENT 'Talla Cofaco',
  `matiz` varchar(2) DEFAULT NULL COMMENT 'Matiz Cofaco',
  `lote` varchar(4) DEFAULT NULL COMMENT 'Lote  Cofaco',
  `um_cofaco` varchar(3) DEFAULT NULL COMMENT 'Unidad de Medida Cofaco',
  `cantidad_bruta` varchar(45) DEFAULT NULL COMMENT 'Cantidad Bruta Cofaco',
  `cantidad_neta` varchar(45) DEFAULT NULL COMMENT 'Cantidad Neta Cofaco',
  `id_externo` varchar(255) DEFAULT NULL,
  `actualizador` varchar(255) DEFAULT NULL,
  `creador` varchar(255) DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT NULL,
  `row_version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`iddetalle`),
  KEY `fk_detalle_cabecera_idx` (`docu_codigo`),
  KEY `idExterno_Det_IDX` (`idExterno`),
  CONSTRAINT `FK558j4h3ulftvclg3jvhepfgvf` FOREIGN KEY (`docu_codigo`) REFERENCES `cabecera` (`docu_codigo`),
  CONSTRAINT `fk_detalle_cabecera` FOREIGN KEY (`docu_codigo`) REFERENCES `cabecera` (`docu_codigo`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Tabla detalle de Cabecera. Donde se guarda informacion referente a las ventas / Documentos afectos a retencion.';



# Dump of table detalle_otro
# ------------------------------------------------------------

DROP TABLE IF EXISTS `detalle_otro`;

CREATE TABLE `detalle_otro` (
  `iddetalle_otro` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Indice de la tabla',
  `iddetalle` int(11) NOT NULL COMMENT 'codigo relacionado con el detalle principal del item. ',
  `motor` varchar(45) DEFAULT NULL COMMENT 'Datios del motor',
  `chasis` varchar(45) DEFAULT NULL COMMENT 'datos del chasis',
  `color` varchar(45) DEFAULT NULL COMMENT 'detalle del color',
  `fabricacion` varchar(45) DEFAULT NULL COMMENT 'Datos referente a fabricacion',
  `marca` varchar(45) DEFAULT NULL COMMENT 'Datos referente a la marca',
  `modelo_periodo` varchar(45) DEFAULT NULL COMMENT 'Datos referente al periodo del modelo',
  `modelo` varchar(45) DEFAULT NULL COMMENT 'Dato referente al modelo',
  `fecha_hora` varchar(45) DEFAULT NULL COMMENT 'Datos referente a fecha y/u hora',
  `origen` varchar(200) DEFAULT NULL COMMENT 'Origen',
  `destino` varchar(200) DEFAULT NULL COMMENT 'Destino',
  `nro_dua` varchar(60) DEFAULT NULL COMMENT 'Datos referente al nro del DUA',
  `serie_dua` varchar(10) DEFAULT NULL COMMENT 'Datos referente a la serie del DUA',
  `aux_cayman_01` varchar(50) DEFAULT NULL COMMENT 'Auxiliares personalizados',
  `aux_cayman_02` varchar(50) DEFAULT NULL COMMENT 'Auxiliares personalizados',
  PRIMARY KEY (`iddetalle_otro`),
  KEY `fk_detalle_otro_detalle1_idx` (`iddetalle`),
  CONSTRAINT `fk_detalle_otro_detalle1` FOREIGN KEY (`iddetalle`) REFERENCES `detalle` (`iddetalle`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Tabla donde se guarda informacion adiciones de bienes o caracteristicas, como Serie, dua, color, modelo, etc';



# Dump of table empresa
# ------------------------------------------------------------

DROP TABLE IF EXISTS `empresa`;

CREATE TABLE `empresa` (
  `ide_empresa` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `empr_ruc` varchar(11) DEFAULT NULL COMMENT 'RUC de la empresa',
  `empr_razon_social` varchar(255) DEFAULT NULL COMMENT 'Razon social de la empresa',
  `empr_tipo` varchar(45) DEFAULT NULL COMMENT 'Tipo: I-Interno, el que emite el documento electronico y va a administrar\nE- Externo, el que recibe el documento electronico, y va a consultar',
  `empr_ri` varchar(200) DEFAULT NULL COMMENT 'Resolucion de Intendenca, solo empresa Interna para Facturas, Boletas y sus Notas por tema de Homologacion y mostrar en la representacion Impresa.\n',
  `empr_link` varchar(500) DEFAULT NULL COMMENT 'Link de consulta, solo empresa Interna para anunciar en el correo y en la representacion Impresa',
  PRIMARY KEY (`ide_empresa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Tabla Maestro de empresa que emiten y reciben documentos';



# Dump of table estado_proceso
# ------------------------------------------------------------

DROP TABLE IF EXISTS `estado_proceso`;

CREATE TABLE `estado_proceso` (
  `idestado_Proceso` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Ide de la tabla',
  `cod_proceso` varchar(2) DEFAULT NULL COMMENT 'Codigo adoptado para el estado',
  `descipcion` varchar(100) DEFAULT NULL COMMENT 'Descipcion del codigo',
  `detalle` varchar(5000) DEFAULT NULL COMMENT 'Detalle amplio del estado',
  PRIMARY KEY (`idestado_Proceso`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table glosa_interna
# ------------------------------------------------------------

DROP TABLE IF EXISTS `glosa_interna`;

CREATE TABLE `glosa_interna` (
  `idglosa_interna` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Pk',
  `orden` int(11) DEFAULT NULL COMMENT 'Orden del Item',
  `docu_codigo` int(11) NOT NULL,
  `detalle` varchar(100) DEFAULT NULL COMMENT 'Detalle de glosa',
  PRIMARY KEY (`idglosa_interna`),
  KEY `fk_glosa_interna_cabecera1_idx` (`docu_codigo`),
  CONSTRAINT `fk_glosa_interna_cabecera1` FOREIGN KEY (`docu_codigo`) REFERENCES `cabecera` (`docu_codigo`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table guia_remision
# ------------------------------------------------------------

DROP TABLE IF EXISTS `guia_remision`;

CREATE TABLE `guia_remision` (
  `idguia_remision` int(11) NOT NULL AUTO_INCREMENT,
  `docu_codigo` int(11) NOT NULL,
  `tipo_documento` varchar(2) DEFAULT NULL,
  `guia_numero` varchar(13) DEFAULT NULL,
  PRIMARY KEY (`idguia_remision`),
  KEY `fk_guia_remision_cabecera1_idx` (`docu_codigo`),
  CONSTRAINT `fk_guia_remision_cabecera1` FOREIGN KEY (`docu_codigo`) REFERENCES `cabecera` (`docu_codigo`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table leyenda
# ------------------------------------------------------------

DROP TABLE IF EXISTS `leyenda`;

CREATE TABLE `leyenda` (
  `id_leyenda` int(11) NOT NULL AUTO_INCREMENT,
  `docu_codigo` int(11) NOT NULL,
  `leyenda_codigo` varchar(10) NOT NULL,
  `leyenda_texto` varchar(500) NOT NULL,
  `actualizador` varchar(255) DEFAULT NULL,
  `creador` varchar(255) DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT NULL,
  `row_version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id_leyenda`),
  KEY `fk_table1_cabecera1_idx` (`docu_codigo`),
  CONSTRAINT `FKpn99u6adhuwpakglg3qapwtnx` FOREIGN KEY (`docu_codigo`) REFERENCES `cabecera` (`docu_codigo`),
  CONSTRAINT `fk_table1_cabecera1` FOREIGN KEY (`docu_codigo`) REFERENCES `cabecera` (`docu_codigo`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table parametros
# ------------------------------------------------------------

DROP TABLE IF EXISTS `parametros`;

CREATE TABLE `parametros` (
  `idparametros` int(11) NOT NULL AUTO_INCREMENT,
  `ruc` varchar(45) DEFAULT NULL,
  `cod_parametro` varchar(45) DEFAULT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `valor` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`idparametros`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table pvm_grupo
# ------------------------------------------------------------

DROP TABLE IF EXISTS `pvm_grupo`;

CREATE TABLE `pvm_grupo` (
  `ide_grupo` bigint(20) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(355) DEFAULT NULL,
  `nombre_grupo` varchar(255) NOT NULL,
  PRIMARY KEY (`ide_grupo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table pvm_usuario
# ------------------------------------------------------------

DROP TABLE IF EXISTS `pvm_usuario`;

CREATE TABLE `pvm_usuario` (
  `ide_usuario` bigint(20) NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `color_usuario` varchar(50) DEFAULT NULL,
  `dni` varchar(10) NOT NULL,
  `email` varchar(50) NOT NULL,
  `nombre_usuario` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `sexo` varchar(50) NOT NULL,
  `ruc` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`ide_usuario`),
  UNIQUE KEY `UK_s53ueirklqhgqy32ptmf9am69` (`dni`),
  UNIQUE KEY `UK_4psi8oj5ie550n6nckh2f6mj9` (`email`),
  UNIQUE KEY `UK_rc5bdw49quyjc7c1ux7d9bqvs` (`nombre_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table pvr_usuario_grupo
# ------------------------------------------------------------

DROP TABLE IF EXISTS `pvr_usuario_grupo`;

CREATE TABLE `pvr_usuario_grupo` (
  `ide_usuario` bigint(20) NOT NULL,
  `ide_grupo` bigint(20) NOT NULL,
  KEY `FK_pa8wjiie7sora7a3rh1tg4r44` (`ide_grupo`),
  KEY `FK_kvybvbbr1exo3ve8omcs8mqfp` (`ide_usuario`),
  CONSTRAINT `FK_kvybvbbr1exo3ve8omcs8mqfp` FOREIGN KEY (`ide_usuario`) REFERENCES `pvm_usuario` (`ide_usuario`),
  CONSTRAINT `FK_pa8wjiie7sora7a3rh1tg4r44` FOREIGN KEY (`ide_grupo`) REFERENCES `pvm_grupo` (`ide_grupo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table reset_registry
# ------------------------------------------------------------

DROP TABLE IF EXISTS `reset_registry`;

CREATE TABLE `reset_registry` (
  `reset_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` varchar(255) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `row_version` bigint(20) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`reset_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table resumenboleta
# ------------------------------------------------------------

DROP TABLE IF EXISTS `resumenboleta`;

CREATE TABLE `resumenboleta` (
  `idresumenboleta` int(11) NOT NULL AUTO_INCREMENT,
  `codigo` int(11) NOT NULL COMMENT 'Codigo de la referencia de la cabecera',
  `resu_fecha` varchar(45) DEFAULT NULL COMMENT 'Fecha del documento',
  `resu_fila` varchar(45) DEFAULT NULL COMMENT 'Numero de file Orden',
  `resu_tipodoc` varchar(45) DEFAULT NULL COMMENT 'Tipo de Documento 03, 07 y 08 boletas y sus notas.',
  `resu_serie` varchar(45) DEFAULT NULL COMMENT 'Numero de serie, version 1.0',
  `resu_inicio` varchar(45) DEFAULT NULL COMMENT 'Inicio de documentos version 1.0',
  `resu_final` varchar(45) DEFAULT NULL COMMENT 'Fin de documento o correlativo, version 1.0',
  `resu_serie_correlativo` varchar(45) DEFAULT NULL COMMENT 'NÃºmero de serie de la boleta de venta que modifica, NÃºmero correlativo de la boleta de venta que modifica - <Serie>-<NÃºmero> ej, B001-000001, BC01-5, version 1.1\n\n\n',
  `resu_clie_numero` varchar(45) DEFAULT '-' COMMENT 'NÃºmero de documento de Identidad del adquirente o usuario si lo tuviera, de lo contrario ''-''',
  `resu_clie_tipodoc` varchar(45) DEFAULT '-' COMMENT 'Tipo de documento de Identidad del adquirente o usuario, sino tiene valor poner "0"',
  `resu_boleta_mod` varchar(45) DEFAULT NULL COMMENT 'NÃºmero de serie de la boleta de venta que modifica, NÃºmero correlativo de la boleta de venta que modifica <Serie>-<NÃºmero>, en caso de Notas de creditos o debito.',
  `resu_tipodoc_mod` varchar(45) DEFAULT NULL COMMENT 'Tipo de documento que modifica - Tipo de documento - CatÃ¡logo No. 01, 03- Boleta',
  `resu_moneda` varchar(45) DEFAULT NULL COMMENT 'moneda del documento',
  `resu_gravada` varchar(45) DEFAULT NULL COMMENT 'Total grabados\n',
  `resu_exonerada` varchar(45) DEFAULT NULL COMMENT 'Total exonerados',
  `resu_inafecta` varchar(45) DEFAULT NULL COMMENT 'Total de inafectos',
  `resu_gratuito` varchar(45) DEFAULT NULL COMMENT 'Total Valor Venta operaciones Gratuitas',
  `resu_otcargos` varchar(45) DEFAULT NULL COMMENT 'Total otros cargos',
  `resu_isc` varchar(45) DEFAULT NULL COMMENT 'total de isc',
  `resu_igv` varchar(45) DEFAULT NULL COMMENT 'total de igv',
  `resu_ottributos` varchar(45) DEFAULT NULL COMMENT 'total otros tributos',
  `resu_total` varchar(45) DEFAULT NULL COMMENT 'Total\n',
  `resu_estado` varchar(45) DEFAULT NULL COMMENT 'Estado del registros\n1- Adiciona\n2.- Modifica\n3.-Anulado de otro dia\n4.-Anulado del mismo dia',
  PRIMARY KEY (`idresumenboleta`),
  KEY `fk_resumenboleta_resumendia_cab1_idx` (`codigo`),
  CONSTRAINT `fk_resumenboleta_resumendia_cab1` FOREIGN KEY (`codigo`) REFERENCES `resumendia_cab` (`codigo`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Detalle de los documentos relacionado al resumen diario.';



# Dump of table resumendia_baja
# ------------------------------------------------------------

DROP TABLE IF EXISTS `resumendia_baja`;

CREATE TABLE `resumendia_baja` (
  `codigo` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Pk de tabla',
  `empr_razonsocial` varchar(200) DEFAULT NULL COMMENT 'Razon social de la Empresa',
  `empr_nroruc` varchar(15) DEFAULT NULL COMMENT 'Nro de Ruc de lsa Empresa',
  `resu_fecha_doc` varchar(15) DEFAULT NULL COMMENT 'Fecha de la generacion del documento de reversion/bajas',
  `resu_identificador` varchar(45) DEFAULT NULL COMMENT 'Identificador de la comunicacion',
  `resu_fecha_com` varchar(45) DEFAULT NULL COMMENT 'Fecha de la generacion de la comunicacion',
  `resu_tipo` varchar(2) NOT NULL COMMENT 'Tipo de resumen\nRA: Resumen de baja (01/03/07/08)\nRR: Resumrn de reversion (20/40/41)',
  `nroticket` varchar(45) DEFAULT NULL COMMENT 'Nro de Ticket devuelto por sunat, para su posterios evaluacion',
  `resu_proce_status` varchar(45) DEFAULT '*' COMMENT 'Estado del proceso',
  `version` varchar(5) DEFAULT NULL COMMENT 'Version del resumnen de bajas',
  `cdr_code` varchar(4) DEFAULT NULL COMMENT 'Codigo de respuesta del cdr',
  `cdr_texto` varchar(2000) DEFAULT NULL COMMENT 'Texto de respuesta del cdr',
  `cdr_link` varchar(200) DEFAULT NULL COMMENT 'Ruta del xml del CDR',
  `cdr_nota` text COMMENT 'Texto de la Nota de observacion del CDR',
  `resu_tipodoc` varchar(2) DEFAULT NULL COMMENT 'Tipo de dicumento de baja, \n01- Factura\n07 - Nota de credito \n08 - Nota de Debito',
  `resu_estado` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Resumen diario de bajas, Solo se afecatas a Facturas y sus Notas.';



# Dump of table resumendia_cab
# ------------------------------------------------------------

DROP TABLE IF EXISTS `resumendia_cab`;

CREATE TABLE `resumendia_cab` (
  `codigo` int(11) NOT NULL AUTO_INCREMENT,
  `empr_razonsocial` varchar(200) DEFAULT NULL COMMENT 'Nombre de la empresa',
  `empr_nroruc` varchar(15) DEFAULT NULL COMMENT 'Numero de Ruc del Emisor',
  `resu_fecha_doc` varchar(15) DEFAULT NULL COMMENT 'Fecha de los documentos',
  `resu_identificador` varchar(45) DEFAULT NULL COMMENT 'Indentificador para el envio a SUNAT se genera con la fecha del envio ',
  `resu_fecha_com` varchar(45) DEFAULT NULL COMMENT 'fecha de la comunicacion o envio ',
  `resu_tipo` varchar(2) DEFAULT 'RC' COMMENT 'Tipo de Resumen por defecto RC, Resumen de comprobantes (Boleta y sus notas)',
  `nroticket` varchar(45) DEFAULT NULL COMMENT 'Numero de ticket del CDR',
  `resu_proce_status` varchar(45) DEFAULT '*' COMMENT 'Estado del prooceso del registro.',
  `version` varchar(5) DEFAULT NULL COMMENT 'Version del proceso  y envio de resumeen de Boletas y sus notas\n1.0 hasta 20161231\n1.1 desde 20170101',
  `cdr_code` varchar(4) DEFAULT NULL COMMENT 'Codigo de respuesta del CDR',
  `cdr_texto` varchar(2000) DEFAULT NULL COMMENT 'Textio de respuesta del CDR',
  `cdr_link` varchar(200) DEFAULT NULL COMMENT 'ruta del xml del CDR',
  `cdr_nota` text COMMENT 'Nota de observacion del CDR',
  `resu_tipodoc` varchar(2) DEFAULT NULL COMMENT 'Tipo de documento.\n03- Boletas\n07- Nota de credito \n08- Nota de Debito.',
  `resu_estado` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Cabecera de Resumen diario de Boletas y sus Notas. a partir del 2018 obligatorio version 1.1';



# Dump of table seguimiento
# ------------------------------------------------------------

DROP TABLE IF EXISTS `seguimiento`;

CREATE TABLE `seguimiento` (
  `idseguimiento` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Codigo autogenerado de la tabla',
  `idExterno` varchar(50) DEFAULT NULL,
  `docu_codigo` int(11) DEFAULT NULL COMMENT 'Codigo del documento',
  `fecha_seguimiento` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de transacion',
  `estado_seguimiento` varchar(45) DEFAULT NULL COMMENT 'Estado de la transaccion\n',
  `cdr_code` varchar(45) DEFAULT NULL COMMENT 'Codigo de respuesta del CDR',
  `cdr_nota` varchar(5000) DEFAULT NULL COMMENT 'Nota de respuesta del CDR',
  `cdr_observacion` varchar(5000) DEFAULT NULL COMMENT 'Observacion con respecto al CDR o al proceso',
  `id_externo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`idseguimiento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Tabls de seguimiento de proceso por de cada Documento electronico, sirve como un Log en tabla.';



# Dump of table tipo_documento
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tipo_documento`;

CREATE TABLE `tipo_documento` (
  `ide_tipo_documento` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nombre_docu` varchar(45) NOT NULL DEFAULT '' COMMENT 'Nombre del Documento, segun el catalogo 01 del anexo 8 ',
  `docu_tipodocumento` varchar(45) NOT NULL DEFAULT '' COMMENT 'Tipo de Documento electronico segun Catalogo 01, del anexo 8.',
  PRIMARY KEY (`ide_tipo_documento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Tipo de documentos electronicos, Factura,Boleta, Nota,.... ';



# Dump of table ubigeo
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ubigeo`;

CREATE TABLE `ubigeo` (
  `id_ubigeo` bigint(20) NOT NULL AUTO_INCREMENT,
  `codigo_ubigeo` varchar(6) NOT NULL,
  `nombre_ubigeo` varchar(50) NOT NULL,
  PRIMARY KEY (`id_ubigeo`),
  UNIQUE KEY `UK_qsrqq0w9eakud321gvwesbgn` (`codigo_ubigeo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table vehiculos_secundarios
# ------------------------------------------------------------

DROP TABLE IF EXISTS `vehiculos_secundarios`;

CREATE TABLE `vehiculos_secundarios` (
  `idvehiculos_secundarios` int(11) NOT NULL AUTO_INCREMENT,
  `guia_factura_id` int(11) NOT NULL,
  `num_placa_c` varchar(8) DEFAULT NULL COMMENT 'Numero de placa',
  PRIMARY KEY (`idvehiculos_secundarios`),
  KEY `fk_vehiculos_secundarios_cabecera_guia_factura1_idx` (`guia_factura_id`),
  CONSTRAINT `fk_vehiculos_secundarios_cabecera_guia_factura1` FOREIGN KEY (`guia_factura_id`) REFERENCES `cabecera_guia_factura` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
