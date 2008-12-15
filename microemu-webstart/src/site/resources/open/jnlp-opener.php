<?php
/**
 *  MicroEmulator
 *  Copyright (C) 2006-2008 Vlad Skarzhevskyy
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  @version $Id$
 */

    $debug = false;

    if (!$debug) {
        header('Content-Type: application/x-java-jnlp-file');
    } else {
        error_reporting(E_ALL);
        header('Content-Type: text/plain');

        echo("GET:");print_r($_GET);
        echo("\n");
    }

    $URL_PARAM = 'jnlp-open-app-url';
    $QUERY_START = '_jnlp-Q_';
    $QUERY_PARAM = '_jnlp-A_';

    $appURL = $_GET[$URL_PARAM];
    $jadURL = $appURL;
    $jnlpURL = $appURL;

    // escape QUERY STRING for jnlp URL, make QUERY part of URL
    $query_pos = strpos($jadURL, $QUERY_START);
    if ($query_pos === false) {
        $jadURL .= '.jad';
        $sep_jad = '?';
        $sep_jnlp= $QUERY_START;
        foreach($_GET as $key => $value) {
            if ($key  != $URL_PARAM) {
                $jadURL .= $sep_jad . $key . '=' . urlencode($value);
                $jnlpURL .= $sep_jnlp . $key . rawurlencode('=' . $value);
                $sep_jad = '&';
                $sep_jnlp = $QUERY_PARAM;
            }
        }
    } else {
        $query = substr($jadURL, $query_pos + strlen($QUERY_START));
        $appURL = substr($jadURL, 0, $query_pos);
        $jadURL = $appURL  . '.jad?';
        $jnlpURL = $appURL . $QUERY_START . rawurlencode($query);

        $query_params = preg_split("/[=]+/", $query, -1);
        $sep = '';
        foreach($query_params as $param) {
            $jadURL .= $sep . str_replace($QUERY_PARAM, '&', urlencode($param));
            $sep = '=';
        }
    }
    $jadURL = 'http://' . $jadURL;

    $patern = '<!--jadRewrite-->';

    $jnlpFileName = "demo.jnlp";

    $jnlpRewritDir = "open/";

    $jnlpFilePath = "../" . $jnlpFileName;
    $fh = fopen($jnlpFilePath, 'r');
    $xml = fread($fh, filesize($jnlpFilePath));
    fclose($fh);
    $xml = ereg_replace($patern . '.+' . $patern, '<argument>' . $jadURL . '</argument>', $xml);

    if (strlen($jnlpURL) > 0) {
        $patern_href = 'href="' . $jnlpFileName . '"';
        $new_href = 'href="' . $jnlpRewritDir . $jnlpURL . '.jnlp"';
        $xml = str_replace($patern_href, $new_href, $xml);
    }

    echo($xml);

?>
